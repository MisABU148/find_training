package m.ilyina.find_trining.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import m.ilyina.find_trining.dto.auth.RegisterRequest;
import m.ilyina.find_trining.dto.user.UserCreateRequest;
import m.ilyina.find_trining.dto.user.UserResponse;
import m.ilyina.find_trining.dto.user.UserUpdateRequest;
import m.ilyina.find_trining.entity.Role;
import m.ilyina.find_trining.entity.User;
import m.ilyina.find_trining.exception.ConflictException;
import m.ilyina.find_trining.exception.NotFoundException;
import m.ilyina.find_trining.mapper.UserMapper;
import m.ilyina.find_trining.repository.UserRepository;
import m.ilyina.find_trining.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Note: passwords (plain or hashed) are never written to the logs here -
 * only ids/emails are logged for traceability.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        log.debug("Fetching all users");
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        return userMapper.toResponse(getOrThrow(id));
    }

    @Override
    public UserResponse create(UserCreateRequest request) {
        String email = assertEmailAvailable(request.email());
        Role role = request.role() == null ? Role.USER : request.role();

        User saved = userRepository.save(
                buildUser(request.fullName(), email, request.password(), request.phone(), role));
        log.info("User created: id={}, email={}, role={}", saved.getId(), saved.getEmail(), saved.getRole());
        return userMapper.toResponse(saved);
    }

    @Override
    public UserResponse register(RegisterRequest request) {
        String email = assertEmailAvailable(request.email());

        // Self-registration never lets the caller pick a role - always USER.
        User saved = userRepository.save(
                buildUser(request.fullName(), email, request.password(), request.phone(), Role.USER));
        log.info("User registered: id={}, email={}", saved.getId(), saved.getEmail());
        return userMapper.toResponse(saved);
    }

    @Override
    public UserResponse update(Long id, UserUpdateRequest request) {
        User user = getOrThrow(id);

        String email = request.email().trim().toLowerCase();
        if (userRepository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            log.warn("Attempt to update user id={} with a duplicate email: {}", id, email);
            throw new ConflictException("Пользователь с email '" + email + "' уже существует");
        }

        user.setFullName(request.fullName().trim());
        user.setEmail(email);
        user.setPhone(request.phone());
        if (request.role() != null) {
            user.setRole(request.role());
        }
        if (request.password() != null && !request.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.password()));
            log.info("Password updated for user id={}", id);
        }

        User saved = userRepository.save(user);
        log.info("User updated: id={}", saved.getId());
        return userMapper.toResponse(saved);
    }

    @Override
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            log.warn("Attempt to delete a non-existent user: id={}", id);
            throw NotFoundException.of("Пользователь", id);
        }
        userRepository.deleteById(id);
        log.info("User deleted: id={}", id);
    }

    private User getOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found: id={}", id);
                    return NotFoundException.of("Пользователь", id);
                });
    }

    /** Normalizes the email and makes sure it isn't already taken. Returns the normalized email. */
    private String assertEmailAvailable(String rawEmail) {
        String email = rawEmail.trim().toLowerCase();
        if (userRepository.existsByEmailIgnoreCase(email)) {
            log.warn("Attempt to use an already registered email: {}", email);
            throw new ConflictException("Пользователь с email '" + email + "' уже существует");
        }
        return email;
    }

    private User buildUser(String fullName, String normalizedEmail, String rawPassword, String phone, Role role) {
        User user = new User();
        user.setFullName(fullName.trim());
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setPhone(phone);
        user.setRole(role);
        return user;
    }
}
