package m.ilyina.find_trining.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import m.ilyina.find_trining.dto.slot.BookSlotRequest;
import m.ilyina.find_trining.dto.slot.TrainingSlotCreateRequest;
import m.ilyina.find_trining.dto.slot.TrainingSlotResponse;
import m.ilyina.find_trining.entity.Coach;
import m.ilyina.find_trining.entity.Gym;
import m.ilyina.find_trining.entity.TrainingSlot;
import m.ilyina.find_trining.entity.User;
import m.ilyina.find_trining.exception.BusinessRuleException;
import m.ilyina.find_trining.exception.ConflictException;
import m.ilyina.find_trining.exception.NotFoundException;
import m.ilyina.find_trining.mapper.TrainingSlotMapper;
import m.ilyina.find_trining.repository.CoachRepository;
import m.ilyina.find_trining.repository.GymRepository;
import m.ilyina.find_trining.repository.TrainingSlotRepository;
import m.ilyina.find_trining.repository.UserRepository;
import m.ilyina.find_trining.service.TrainingSlotService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Расписание тренировок: тренер "арендует" зал на конкретный час
 * (пн-пт, с 10:00 до 23:00, ровно на час), пользователи записываются на
 * свободные слоты. Правила расписания проверяются здесь, а не на уровне
 * entity/DTO, так как они завязаны на несколько полей сразу.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TrainingSlotServiceImpl implements TrainingSlotService {

    private static final int FIRST_SLOT_HOUR = 10;
    private static final int LAST_SLOT_START_HOUR = 22; // последний слот 22:00-23:00

    private final TrainingSlotRepository slotRepository;
    private final GymRepository gymRepository;
    private final CoachRepository coachRepository;
    private final UserRepository userRepository;
    private final TrainingSlotMapper slotMapper;

    @Override
    @Transactional(readOnly = true)
    public List<TrainingSlotResponse> listByGym(Long gymId) {
        if (!gymRepository.existsById(gymId)) {
            throw NotFoundException.of("Зал", gymId);
        }
        return slotRepository.findByGymIdOrderByStartsAt(gymId).stream()
                .map(slotMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainingSlotResponse> listByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw NotFoundException.of("Пользователь", userId);
        }
        return slotRepository.findByBookedByIdOrderByStartsAt(userId).stream()
                .map(slotMapper::toResponse)
                .toList();
    }

    @Override
    public TrainingSlotResponse create(TrainingSlotCreateRequest request) {
        Gym gym = gymRepository.findById(request.gymId())
                .orElseThrow(() -> NotFoundException.of("Зал", request.gymId()));
        Coach coach = coachRepository.findById(request.coachId())
                .orElseThrow(() -> NotFoundException.of("Тренер", request.coachId()));

        validateBusinessHours(request.startsAt());

        if (slotRepository.existsByGymIdAndStartsAt(gym.getId(), request.startsAt())) {
            log.warn("Attempt to rent an already-taken gym slot: gymId={}, startsAt={}", gym.getId(), request.startsAt());
            throw new ConflictException("В этот час зал уже занят другим тренером");
        }
        if (slotRepository.existsByCoachIdAndStartsAt(coach.getId(), request.startsAt())) {
            log.warn("Attempt to double-book a coach: coachId={}, startsAt={}", coach.getId(), request.startsAt());
            throw new ConflictException("У тренера уже есть занятие на это время");
        }

        TrainingSlot slot = new TrainingSlot();
        slot.setGym(gym);
        slot.setCoach(coach);
        slot.setStartsAt(request.startsAt());

        TrainingSlot saved = slotRepository.save(slot);
        log.info("Training slot created: id={}, gymId={}, coachId={}, startsAt={}",
                saved.getId(), gym.getId(), coach.getId(), saved.getStartsAt());
        return slotMapper.toResponse(saved);
    }

    @Override
    public TrainingSlotResponse book(Long slotId, BookSlotRequest request) {
        TrainingSlot slot = getOrThrow(slotId);
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> NotFoundException.of("Пользователь", request.userId()));

        if (slot.getBookedBy() != null) {
            log.warn("Attempt to book an already-taken slot: slotId={}", slotId);
            throw new ConflictException("Слот уже забронирован");
        }
        if (slot.getStartsAt().isBefore(LocalDateTime.now())) {
            throw new BusinessRuleException("Время этого занятия уже прошло");
        }
        if (slotRepository.existsByBookedByIdAndStartsAt(user.getId(), slot.getStartsAt())) {
            throw new ConflictException("У вас уже есть запись на это время");
        }

        slot.setBookedBy(user);
        slot.setBookedAt(LocalDateTime.now());

        TrainingSlot saved = slotRepository.save(slot);
        log.info("Slot booked: slotId={}, userId={}", saved.getId(), user.getId());
        return slotMapper.toResponse(saved);
    }

    @Override
    public TrainingSlotResponse cancelBooking(Long slotId, Long userId) {
        TrainingSlot slot = getOrThrow(slotId);

        if (slot.getBookedBy() == null) {
            throw new BusinessRuleException("Слот не забронирован - нечего отменять");
        }
        if (!slot.getBookedBy().getId().equals(userId)) {
            log.warn("User {} tried to cancel a booking owned by user {}", userId, slot.getBookedBy().getId());
            throw new ConflictException("Эта запись принадлежит другому пользователю");
        }
        if (slot.getStartsAt().isBefore(LocalDateTime.now())) {
            throw new BusinessRuleException("Нельзя отменить запись на уже прошедшее занятие");
        }

        slot.setBookedBy(null);
        slot.setBookedAt(null);

        TrainingSlot saved = slotRepository.save(slot);
        log.info("Booking cancelled: slotId={}, userId={}", saved.getId(), userId);
        return slotMapper.toResponse(saved);
    }

    @Override
    public void delete(Long slotId) {
        TrainingSlot slot = getOrThrow(slotId);
        if (slot.getBookedBy() != null) {
            throw new ConflictException("Нельзя удалить слот с активной записью - сначала отмените запись");
        }
        slotRepository.deleteById(slotId);
        log.info("Training slot deleted: id={}", slotId);
    }

    private TrainingSlot getOrThrow(Long id) {
        return slotRepository.findById(id)
                .orElseThrow(() -> NotFoundException.of("Слот", id));
    }

    private void validateBusinessHours(LocalDateTime startsAt) {
        DayOfWeek day = startsAt.getDayOfWeek();
        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
            throw new BusinessRuleException("Тренировки проводятся только с понедельника по пятницу");
        }
        if (startsAt.getMinute() != 0 || startsAt.getSecond() != 0 || startsAt.getNano() != 0) {
            throw new BusinessRuleException("Слот должен начинаться ровно в начале часа");
        }
        int hour = startsAt.getHour();
        if (hour < FIRST_SLOT_HOUR || hour > LAST_SLOT_START_HOUR) {
            throw new BusinessRuleException("Слоты доступны с 10:00 до 23:00 (последний слот начинается в 22:00)");
        }
    }
}
