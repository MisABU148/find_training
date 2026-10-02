package m.ilyina.find_trining.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import m.ilyina.find_trining.dto.gym.GymCreateRequest;
import m.ilyina.find_trining.dto.gym.GymResponse;
import m.ilyina.find_trining.dto.gym.GymUpdateRequest;
import m.ilyina.find_trining.entity.Gym;
import m.ilyina.find_trining.exception.NotFoundException;
import m.ilyina.find_trining.mapper.GymMapper;
import m.ilyina.find_trining.repository.GymRepository;
import m.ilyina.find_trining.service.GymService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class GymServiceImpl implements GymService {

    private final GymRepository gymRepository;
    private final GymMapper gymMapper;

    @Override
    @Transactional(readOnly = true)
    public List<GymResponse> findAll() {
        log.debug("Fetching all gyms");
        return gymRepository.findAll().stream()
                .map(gymMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public GymResponse findById(Long id) {
        return gymMapper.toResponse(getOrThrow(id));
    }

    @Override
    public GymResponse create(GymCreateRequest request) {
        Gym gym = gymMapper.toEntity(request);
        Gym saved = gymRepository.save(gym);
        log.info("Gym created: id={}, name={}", saved.getId(), saved.getName());
        return gymMapper.toResponse(saved);
    }

    @Override
    public GymResponse update(Long id, GymUpdateRequest request) {
        Gym gym = getOrThrow(id);
        gymMapper.updateEntity(gym, request);
        Gym saved = gymRepository.save(gym);
        log.info("Gym updated: id={}", saved.getId());
        return gymMapper.toResponse(saved);
    }

    @Override
    public void delete(Long id) {
        if (!gymRepository.existsById(id)) {
            log.warn("Attempt to delete a non-existent gym: id={}", id);
            throw NotFoundException.of("Зал", id);
        }
        gymRepository.deleteById(id);
        log.info("Gym deleted: id={}", id);
    }

    private Gym getOrThrow(Long id) {
        return gymRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Gym not found: id={}", id);
                    return NotFoundException.of("Зал", id);
                });
    }
}
