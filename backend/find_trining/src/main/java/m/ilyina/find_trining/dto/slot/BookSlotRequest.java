package m.ilyina.find_trining.dto.slot;

import jakarta.validation.constraints.NotNull;

public record BookSlotRequest(

        @NotNull(message = "Пользователь обязателен")
        Long userId
) {
}
