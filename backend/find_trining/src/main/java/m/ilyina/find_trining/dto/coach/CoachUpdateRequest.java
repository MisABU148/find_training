package m.ilyina.find_trining.dto.coach;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record CoachUpdateRequest(

        @NotBlank(message = "Имя тренера обязательно")
        @Size(max = 100, message = "Имя не должно превышать 100 символов")
        String fullName,

        @NotBlank(message = "Email обязателен")
        @Email(message = "Некорректный формат email")
        @Size(max = 150, message = "Email не должен превышать 150 символов")
        String email,

        @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "Некорректный формат телефона")
        String phone,

        @Size(max = 2000, message = "Описание не должно превышать 2000 символов")
        String bio,

        @Min(value = 0, message = "Стаж не может быть отрицательным")
        @Max(value = 60, message = "Стаж указан некорректно")
        Integer experienceYears,

        Long gymId,

        Set<Long> sportIds
) {
}
