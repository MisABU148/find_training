package m.ilyina.find_trining.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import m.ilyina.find_trining.dto.slot.BookSlotRequest;
import m.ilyina.find_trining.dto.slot.TrainingSlotCreateRequest;
import m.ilyina.find_trining.dto.slot.TrainingSlotResponse;
import m.ilyina.find_trining.exception.BusinessRuleException;
import m.ilyina.find_trining.service.TrainingSlotService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/training-slots")
@RequiredArgsConstructor
public class TrainingSlotController {

    private final TrainingSlotService trainingSlotService;

    /**
     * Ровно один из параметров обязателен: {@code gymId} - расписание зала
     * (экран записи на тренировку), {@code bookedByUserId} - брони
     * конкретного пользователя (экран "Мои записи").
     */
    @GetMapping
    public List<TrainingSlotResponse> list(
            @RequestParam(required = false) Long gymId,
            @RequestParam(required = false) Long bookedByUserId
    ) {
        if (gymId != null) {
            return trainingSlotService.listByGym(gymId);
        }
        if (bookedByUserId != null) {
            return trainingSlotService.listByUser(bookedByUserId);
        }
        throw new BusinessRuleException("Укажите gymId или bookedByUserId");
    }

    @PostMapping
    public ResponseEntity<TrainingSlotResponse> create(@Valid @RequestBody TrainingSlotCreateRequest request) {
        TrainingSlotResponse created = trainingSlotService.create(request);
        return ResponseEntity.created(URI.create("/api/training-slots/" + created.id())).body(created);
    }

    @PostMapping("/{id}/book")
    public TrainingSlotResponse book(@PathVariable Long id, @Valid @RequestBody BookSlotRequest request) {
        return trainingSlotService.book(id, request);
    }

    @DeleteMapping("/{id}/book")
    public TrainingSlotResponse cancelBooking(@PathVariable Long id, @RequestParam Long userId) {
        return trainingSlotService.cancelBooking(id, userId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        trainingSlotService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
