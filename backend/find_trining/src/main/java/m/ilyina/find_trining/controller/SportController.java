package m.ilyina.find_trining.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import m.ilyina.find_trining.dto.sport.SportCreateRequest;
import m.ilyina.find_trining.dto.sport.SportResponse;
import m.ilyina.find_trining.service.SportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

/**
 * Sports are a simple reference list used by coaches - only adding and
 * removing entries is supported (no update endpoint).
 */
@RestController
@RequestMapping("/api/sports")
@RequiredArgsConstructor
public class SportController {

    private final SportService sportService;

    @GetMapping
    public List<SportResponse> findAll() {
        return sportService.findAll();
    }

    @PostMapping
    public ResponseEntity<SportResponse> create(@Valid @RequestBody SportCreateRequest request) {
        SportResponse created = sportService.create(request);
        return ResponseEntity.created(URI.create("/api/sports/" + created.id())).body(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        sportService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
