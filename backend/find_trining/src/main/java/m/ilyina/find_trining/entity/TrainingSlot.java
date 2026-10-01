package m.ilyina.find_trining.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;

/**
 * One concrete, dated hour of training at a gym, "rented" by a coach.
 * Business hours (Mon-Fri, 10:00-23:00, on the hour) are enforced in the
 * service layer, not here - the entity only models the shape of the data.
 */
@Entity
@Table(name = "training_slots", uniqueConstraints = {
        @UniqueConstraint(name = "uq_training_slots_gym_time", columnNames = {"gym_id", "starts_at"}),
        @UniqueConstraint(name = "uq_training_slots_coach_time", columnNames = {"coach_id", "starts_at"})
})
@Getter
@Setter
@NoArgsConstructor
public class TrainingSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "gym_id", nullable = false)
    private Gym gym;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "coach_id", nullable = false)
    private Coach coach;

    @Column(name = "starts_at", nullable = false)
    private LocalDateTime startsAt;

    /** null = свободен; заполнено = слот забронирован этим пользователем. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booked_by")
    private User bookedBy;

    @Column(name = "booked_at")
    private LocalDateTime bookedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @jakarta.persistence.PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
    }

    @Transient
    public LocalDateTime getEndsAt() {
        return startsAt.plusHours(1);
    }
}
