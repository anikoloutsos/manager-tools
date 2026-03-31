package com.managertools.engineer;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/engineers")
public class EngineerController {

    private final EngineerService service;

    public EngineerController(EngineerService service) {
        this.service = service;
    }

    @GetMapping
    public List<EngineerResponse> findAll() {
        return service.findAll().stream()
                .map(EngineerResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public EngineerResponse findById(@PathVariable Long id) {
        return EngineerResponse.from(service.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EngineerResponse create(@Valid @RequestBody EngineerRequest request) {
        return EngineerResponse.from(service.create(request));
    }

    @PutMapping("/{id}")
    public EngineerResponse update(@PathVariable Long id, @Valid @RequestBody EngineerRequest request) {
        return EngineerResponse.from(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
