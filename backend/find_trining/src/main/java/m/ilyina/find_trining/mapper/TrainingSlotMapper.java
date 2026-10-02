package m.ilyina.find_trining.mapper;

import lombok.RequiredArgsConstructor;
import m.ilyina.find_trining.dto.slot.TrainingSlotResponse;
import m.ilyina.find_trining.entity.TrainingSlot;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TrainingSlotMapper {

    private final GymMapper gymMapper;
    private final CoachMapper coachMapper;
    private final UserMapper userMapper;

    public TrainingSlotResponse toResponse(TrainingSlot slot) {
        return new TrainingSlotResponse(
                slot.getId(),
                gymMapper.toResponse(slot.getGym()),
                coachMapper.toResponse(slot.getCoach()),
                slot.getStartsAt(),
                slot.getEndsAt(),
                slot.getBookedBy() == null ? null : userMapper.toResponse(slot.getBookedBy())
        );
    }
}
