package m.ilyina.find_trining.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import m.ilyina.find_trining.dto.sport.SportCreateRequest;
import m.ilyina.find_trining.dto.sport.SportResponse;
import m.ilyina.find_trining.entity.Sport;
import m.ilyina.find_trining.exception.ConflictException;
import m.ilyina.find_trining.exception.NotFoundException;
import m.ilyina.find_trining.mapper.SportMapper;
import m.ilyina.find_trining.repository.SportRepository;
import m.ilyina.find_trining.service.SportService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SportServiceImpl implements SportService {

    private final SportRepository sportRepository;
    private final SportMapper sportMapper;

    @Override
    @Transactional(readOnly = true)
    public List<SportResponse> findAll() {
        log.debug("Fetching all sports");
        return sportRepository.findAll().stream()
                .map(sportMapper::toResponse)
                .toList();
    }

    @Override
    public SportResponse create(SportCreateRequest request) {
        String name = request.name().trim();
        if (sportRepository.existsByNameIgnoreCase(name)) {
            log.warn("Attempt to create a duplicate sport: name={}", name);
            throw new ConflictException("Вид спорта с названием '" + name + "' уже существует");
        }
        Sport sport = new Sport();
        sport.setName(name);
        Sport saved = sportRepository.save(sport);
        log.info("Sport created: id={}, name={}", saved.getId(), saved.getName());
        return sportMapper.toResponse(saved);
    }

    @Override
    public void delete(Long id) {
        if (!sportRepository.existsById(id)) {
            log.warn("Attempt to delete a non-existent sport: id={}", id);
            throw NotFoundException.of("Вид спорта", id);
        }
        sportRepository.deleteById(id);
        log.info("Sport deleted: id={}", id);
    }
}
