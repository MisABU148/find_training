package m.ilyina.find_trining.dto.gym;

import java.time.Instant;

public record GymResponse(
        Long id,
        String name,
        String address,
        String city,
        String phone,
        String description,
        Instant createdAt
) {
}
