package m.ilyina.find_trining.repository;

import m.ilyina.find_trining.entity.Sport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SportRepository extends JpaRepository<Sport, Long> {

    boolean existsByNameIgnoreCase(String name);

    Optional<Sport> findByNameIgnoreCase(String name);
}
