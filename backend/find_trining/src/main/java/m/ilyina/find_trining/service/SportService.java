package m.ilyina.find_trining.service;

import m.ilyina.find_trining.dto.sport.SportCreateRequest;
import m.ilyina.find_trining.dto.sport.SportResponse;

import java.util.List;

/**
 * Sports are a simple reference list used by coaches - only adding and
 * removing entries is supported (no update operation).
 */
public interface SportService {

    List<SportResponse> findAll();

    SportResponse create(SportCreateRequest request);

    void delete(Long id);
}
