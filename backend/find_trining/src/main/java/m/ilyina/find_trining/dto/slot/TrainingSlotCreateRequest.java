package m.ilyina.find_trining.dto.slot;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Тренер "арендует" зал на конкретный час. Бизнес-правила (пн-пт,
 * 10:00-23:00, ровно на час) проверяются в сервисе, а не здесь - тут
 * только базовая форма запроса.
 */
public record TrainingSlotCreateRequest(

        @NotNull(message = "Зал обязателен")
        Long gymId,

        @NotNull(message = "Тренер обязателен")
        Long coachId,

        @NotNull(message = "Дата и время начала обязательны")
        @Future(message = "Слот должен быть в будущем")
        LocalDateTime startsAt
) {
}
