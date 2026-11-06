package m.ilyina.find_trining.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import m.ilyina.find_trining.repository.TrainingSlotRepository;
import org.springframework.stereotype.Component;

@Component
public class BusinessMetrics {

    private final MeterRegistry registry;
    private final Counter slotCreated;
    private final Counter slotBooked;
    private final Counter slotCancelled;

    public BusinessMetrics(MeterRegistry registry, TrainingSlotRepository slotRepository) {
        this.registry = registry;

        this.slotCreated = Counter.builder("training_slot_created_total")
                .description("Created training slots").register(registry);
        this.slotBooked = Counter.builder("training_slot_booked_total")
                .description("Successful slot bookings").register(registry);
        this.slotCancelled = Counter.builder("training_slot_cancelled_total")
                        .description("Cancelled slot bookings").register(registry);

        Gauge.builder("training_slots_free", slotRepository,
                        TrainingSlotRepository::countByBookedByIsNull)
                .description("Free training slots").register(registry);
        Gauge.builder("training_slots_booked", slotRepository,
                        TrainingSlotRepository::countByBookedByIsNotNull)
                .description("Currently booked slots").register(registry);
    }

    public void slotCreated() { slotCreated.increment(); }
    public void slotBooked()  { slotBooked.increment(); }
    public void slotUnBooked()  {slotCancelled.increment();}

    public void bookingFailed(String reason) {
        Counter.builder("training_slot_booking_failed_total")
                .description("Failed slot bookings")
                .tag("reason", reason)
                .register(registry)
                .increment();
    }
}
