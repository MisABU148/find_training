package m.ilyina.find_trining.dto.sport;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SportCreateRequest(

        @NotBlank(message = "Название вида спорта обязательно")
        @Size(max = 60, message = "Название не должно превышать 60 символов")
        String name
) {
}
