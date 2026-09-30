package m.ilyina.find_trining.service;

import m.ilyina.find_trining.dto.auth.RegisterRequest;
import m.ilyina.find_trining.dto.user.UserCreateRequest;
import m.ilyina.find_trining.dto.user.UserResponse;
import m.ilyina.find_trining.dto.user.UserUpdateRequest;

import java.util.List;

public interface UserService {

    List<UserResponse> findAll();

    UserResponse findById(Long id);

    UserResponse create(UserCreateRequest request);

    /**
     * Public self-registration: always creates the account with role
     * {@code USER}, regardless of what {@link UserCreateRequest} allows
     * for admin-driven creation.
     */
    UserResponse register(RegisterRequest request);

    UserResponse update(Long id, UserUpdateRequest request);

    void delete(Long id);
}
