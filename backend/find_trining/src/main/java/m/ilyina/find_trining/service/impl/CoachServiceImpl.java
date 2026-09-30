package m.ilyina.find_trining.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import m.ilyina.find_trining.dto.coach.CoachCreateRequest;
import m.ilyina.find_trining.dto.coach.CoachResponse;
import m.ilyina.find_trining.dto.coach.CoachUpdateRequest;
import m.ilyina.find_trining.entity.Coach;
import m.ilyina.find_trining.entity.Gym;
import m.ilyina.find_trining.entity.Sport;
import m.ilyina.find_trining.exception.ConflictException;
import m.ilyina.find_trining.exception.NotFoundException;
import m.ilyina.find_trining.mapper.CoachMapper;
import m.ilyina.find_trining.repository.CoachRepository;
import m.ilyina.find_trining.repository.GymRepository;
import m.ilyina.find_trining.repository.SportRepository;
import m.ilyina.find_trining.service.CoachService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CoachServiceImpl implements CoachService {

    private final CoachRepository coachRepository;
    private final GymRepository gymRepository;
    private final SportRepository sportRepository;
    private final CoachMapper coachMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CoachResponse> findAll() {
        log.debug("Fetching all coaches");
        return coachRepository.findAll().stream()
                .map(coachMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CoachResponse findById(Long id) {
        return coachMapper.toResponse(getOrThrow(id));
    }

    @Override
    public CoachResponse create(CoachCreateRequest request) {
        String email = request.email().trim().toLowerCase();
        if (coachRepository.existsByEmailIgnoreCase(email)) {
            log.warn("Attempt to create a coach with a duplicate email: {}", email);
            throw new ConflictException("Тренер с email '" + email + "' уже существует");
        }

        Coach coach = coachMapper.toEntity(request);
        coach.setGym(resolveGym(request.gymId()));
        coach.setSports(resolveSports(request.sportIds()));

        Coach saved = coachRepository.save(coach);
        log.info("Coach created: id={}, email={}", saved.getId(), saved.getEmail());
        return coachMapper.toResponse(saved);
    }

    @Override
    public CoachResponse update(Long id, CoachUpdateRequest request) {
        Coach coach = getOrThrow(id);

        String email = request.email().trim().toLowerCase();
        if (coachRepository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            log.warn("Attempt to update coach id={} with a duplicate email: {}", id, email);
            throw new ConflictException("Тренер с email '" + email + "' уже существует");
        }

        coachMapper.updateEntity(coach, request);
        coach.setGym(resolveGym(request.gymId()));
        coach.setSports(resolveSports(request.sportIds()));

        Coach saved = coachRepository.save(coach);
        log.info("Coach updated: id={}", saved.getId());
        return coachMapper.toResponse(saved);
    }

    @Override
    public void delete(Long id) {
        if (!coachRepository.existsById(id)) {
            log.warn("Attempt to delete a non-existent coach: id={}", id);
            throw NotFoundException.of("Тренер", id);
        }
        coachRepository.deleteById(id);
        log.info("Coach deleted: id={}", id);
    }

    private Coach getOrThrow(Long id) {
        return coachRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Coach not found: id={}", id);
                    return NotFoundException.of("Тренер", id);
                });
    }

    private Gym resolveGym(Long gymId) {
        if (gymId == null) {
            return null;
        }
        return gymRepository.findById(gymId)
                .orElseThrow(() -> {
                    log.warn("Referenced gym not found: gymId={}", gymId);
                    return NotFoundException.of("Зал", gymId);
                });
    }

    private Set<Sport> resolveSports(Set<Long> sportIds) {
        if (sportIds == null || sportIds.isEmpty()) {
            return new HashSet<>();
        }
        List<Sport> found = sportRepository.findAllById(sportIds);
        if (found.size() != sportIds.size()) {
            log.warn("Some referenced sports were not found: requested={}, found={}", sportIds.size(), found.size());
            throw new NotFoundException("Один или несколько видов спорта не найдены");
        }
        return new HashSet<>(found);
    }
}
