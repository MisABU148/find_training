package m.ilyina.find_trining.dto.coach;

import m.ilyina.find_trining.dto.gym.GymResponse;
import m.ilyina.find_trining.dto.sport.SportResponse;

import java.time.Instant;
import java.util.Set;

public record CoachResponse(
        Long id,
        String fullName,
        String email,
        String phone,
        String bio,
        Integer experienceYears,
        GymResponse gym,
        Set<SportResponse> sports,
        Instant createdAt
) {
}
