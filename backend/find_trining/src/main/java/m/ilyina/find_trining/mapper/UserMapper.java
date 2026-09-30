package m.ilyina.find_trining.mapper;

import m.ilyina.find_trining.dto.user.UserResponse;
import m.ilyina.find_trining.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}
