package m.ilyina.find_trining.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import m.ilyina.find_trining.dto.auth.RegisterRequest;
import m.ilyina.find_trining.dto.user.UserResponse;
import m.ilyina.find_trining.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

/**
 * Public, unauthenticated endpoints for account self-service.
 * Login (JWT issuance) will be added here later.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse created = userService.register(request);
        return ResponseEntity.created(URI.create("/api/users/" + created.id())).body(created);
    }
}
