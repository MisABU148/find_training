package m.ilyina.find_trining.service;

import m.ilyina.find_trining.dto.coach.CoachCreateRequest;
import m.ilyina.find_trining.dto.coach.CoachResponse;
import m.ilyina.find_trining.dto.coach.CoachUpdateRequest;

import java.util.List;

public interface CoachService {

    List<CoachResponse> findAll();

    CoachResponse findById(Long id);

    CoachResponse create(CoachCreateRequest request);

    CoachResponse update(Long id, CoachUpdateRequest request);

    void delete(Long id);
}
