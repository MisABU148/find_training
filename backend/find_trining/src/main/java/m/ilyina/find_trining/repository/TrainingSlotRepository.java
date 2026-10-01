package m.ilyina.find_trining.repository;

import m.ilyina.find_trining.entity.TrainingSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TrainingSlotRepository extends JpaRepository<TrainingSlot, Long> {

    List<TrainingSlot> findByGymIdOrderByStartsAt(Long gymId);

    List<TrainingSlot> findByBookedByIdOrderByStartsAt(Long userId);

    boolean existsByGymIdAndStartsAt(Long gymId, LocalDateTime startsAt);

    boolean existsByCoachIdAndStartsAt(Long coachId, LocalDateTime startsAt);

    boolean existsByBookedByIdAndStartsAt(Long userId, LocalDateTime startsAt);
}
