package m.ilyina.find_trining.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Public self-registration payload. Unlike {@code UserCreateRequest}, this
 * intentionally has no {@code role} field - every self-registered account
 * is created with role {@code USER}, never chosen by the caller.
 */
public record RegisterRequest(

        @NotBlank(message = "Имя обязательно")
        @Size(max = 100, message = "Имя не должно превышать 100 символов")
        String fullName,

        @NotBlank(message = "Email обязателен")
        @Email(message = "Некорректный формат email")
        @Size(max = 150, message = "Email не должен превышать 150 символов")
        String email,

        @NotBlank(message = "Пароль обязателен")
        @Size(min = 8, max = 72, message = "Пароль должен быть от 8 до 72 символов")
        String password,

        @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "Некорректный формат телефона")
        String phone
) {
}
