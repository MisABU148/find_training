package m.ilyina.find_trining.service;

import m.ilyina.find_trining.dto.gym.GymCreateRequest;
import m.ilyina.find_trining.dto.gym.GymResponse;
import m.ilyina.find_trining.dto.gym.GymUpdateRequest;

import java.util.List;

public interface GymService {

    List<GymResponse> findAll();

    GymResponse findById(Long id);

    GymResponse create(GymCreateRequest request);

    GymResponse update(Long id, GymUpdateRequest request);

    void delete(Long id);
}
