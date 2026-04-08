package io.anikoloutsos.manager_tools.engineer;

import io.anikoloutsos.manager_tools.engineer.dto.CreateEngineerRequest;
import io.anikoloutsos.manager_tools.engineer.dto.EngineerResponse;
import io.anikoloutsos.manager_tools.engineer.dto.UpdateEngineerRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/engineers")
public class EngineerController {

    private final EngineerService service;

    public EngineerController(EngineerService service) {
        this.service = service;
    }

    @GetMapping
    public List<EngineerResponse> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public EngineerResponse getById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EngineerResponse create(@RequestBody @Valid CreateEngineerRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable UUID id, @RequestBody @Valid UpdateEngineerRequest request) {
        service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
