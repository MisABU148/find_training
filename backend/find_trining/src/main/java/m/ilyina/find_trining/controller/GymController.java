package m.ilyina.find_trining.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import m.ilyina.find_trining.dto.gym.GymCreateRequest;
import m.ilyina.find_trining.dto.gym.GymResponse;
import m.ilyina.find_trining.dto.gym.GymUpdateRequest;
import m.ilyina.find_trining.service.GymService;
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
@RequestMapping("/api/gyms")
@RequiredArgsConstructor
public class GymController {

    private final GymService gymService;

    @GetMapping
    public List<GymResponse> findAll() {
        return gymService.findAll();
    }

    @GetMapping("/{id}")
    public GymResponse findById(@PathVariable Long id) {
        return gymService.findById(id);
    }

    @PostMapping
    public ResponseEntity<GymResponse> create(@Valid @RequestBody GymCreateRequest request) {
        GymResponse created = gymService.create(request);
        return ResponseEntity.created(URI.create("/api/gyms/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    public GymResponse update(@PathVariable Long id, @Valid @RequestBody GymUpdateRequest request) {
        return gymService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        gymService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
