package m.ilyina.find_trining.dto.gym;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record GymUpdateRequest(

        @NotBlank(message = "Название зала обязательно")
        @Size(max = 150, message = "Название не должно превышать 150 символов")
        String name,

        @NotBlank(message = "Адрес обязателен")
        @Size(max = 255, message = "Адрес не должен превышать 255 символов")
        String address,

        @NotBlank(message = "Город обязателен")
        @Size(max = 100, message = "Город не должен превышать 100 символов")
        String city,

        @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "Некорректный формат телефона")
        String phone,

        @Size(max = 2000, message = "Описание не должно превышать 2000 символов")
        String description
) {
}
