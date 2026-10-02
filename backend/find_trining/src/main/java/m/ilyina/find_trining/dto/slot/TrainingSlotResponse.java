package m.ilyina.find_trining.dto.slot;

import m.ilyina.find_trining.dto.coach.CoachResponse;
import m.ilyina.find_trining.dto.gym.GymResponse;
import m.ilyina.find_trining.dto.user.UserResponse;

import java.time.LocalDateTime;

public record TrainingSlotResponse(
        Long id,
        GymResponse gym,
        CoachResponse coach,
        LocalDateTime startsAt,
        LocalDateTime endsAt,
        /** null, если слот ещё свободен. */
        UserResponse bookedBy
) {
}
