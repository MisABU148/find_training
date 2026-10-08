package m.ilyina.find_trining.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import m.ilyina.find_trining.dto.coach.CoachCreateRequest;
import m.ilyina.find_trining.dto.coach.CoachResponse;
import m.ilyina.find_trining.dto.coach.CoachUpdateRequest;
import m.ilyina.find_trining.service.CoachService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/coaches")
@RequiredArgsConstructor
public class CoachController {

    private final CoachService coachService;

    @GetMapping
    public List<CoachResponse> findAll() {
        return coachService.findAll();
    }

    @GetMapping("/{id}")
    public CoachResponse findById(@PathVariable Long id) {
        return coachService.findById(id);
    }

    @PostMapping
    public ResponseEntity<CoachResponse> create(@Valid @RequestBody CoachCreateRequest request) {
        CoachResponse created = coachService.create(request);
        return ResponseEntity.created(URI.create("/api/coaches/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    public CoachResponse update(@PathVariable Long id, @Valid @RequestBody CoachUpdateRequest request) {
        return coachService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        coachService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
