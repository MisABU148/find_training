package m.ilyina.find_trining.service;

import m.ilyina.find_trining.dto.slot.BookSlotRequest;
import m.ilyina.find_trining.dto.slot.TrainingSlotCreateRequest;
import m.ilyina.find_trining.dto.slot.TrainingSlotResponse;

import java.util.List;

public interface TrainingSlotService {

    /** Все слоты зала (и свободные, и занятые), отсортированы по времени. */
    List<TrainingSlotResponse> listByGym(Long gymId);

    /** Все брони конкретного пользователя (прошлые и будущие), отсортированы по времени. */
    List<TrainingSlotResponse> listByUser(Long userId);

    /** Тренер арендует зал на конкретный час. */
    TrainingSlotResponse create(TrainingSlotCreateRequest request);

    /** Пользователь записывается на свободный слот. */
    TrainingSlotResponse book(Long slotId, BookSlotRequest request);

    /** Пользователь отменяет свою запись (слот снова становится свободным). */
    TrainingSlotResponse cancelBooking(Long slotId, Long userId);

    /** Тренер убирает ещё не забронированный слот из расписания. */
    void delete(Long slotId);
}
