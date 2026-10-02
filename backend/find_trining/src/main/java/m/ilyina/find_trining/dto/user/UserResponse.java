package m.ilyina.find_trining.dto.user;

import m.ilyina.find_trining.entity.Role;

import java.time.Instant;

/** Never includes the password (or its hash). */
public record UserResponse(
        Long id,
        String fullName,
        String email,
        String phone,
        Role role,
        Instant createdAt
) {
}
