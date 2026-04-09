package io.anikoloutsos.manager_tools.note;

import io.anikoloutsos.manager_tools.note.dto.CreateNoteRequest;
import io.anikoloutsos.manager_tools.note.dto.NoteResponse;
import io.anikoloutsos.manager_tools.note.dto.UpdateNoteRequest;
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
@RequestMapping("/engineers/{engineerId}/notes")
public class NoteController {

    private final NoteService service;

    public NoteController(NoteService service) {
        this.service = service;
    }

    @GetMapping
    public List<NoteResponse> getAll(@PathVariable UUID engineerId) {
        return service.findAllByEngineerId(engineerId);
    }

    @GetMapping("/{id}")
    public NoteResponse getById(@PathVariable UUID engineerId, @PathVariable UUID id) {
        return service.findById(engineerId, id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NoteResponse create(@PathVariable UUID engineerId, @RequestBody @Valid CreateNoteRequest request) {
        return service.create(engineerId, request);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable UUID engineerId, @PathVariable UUID id, @RequestBody @Valid UpdateNoteRequest request) {
        service.update(engineerId, id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID engineerId, @PathVariable UUID id) {
        service.delete(engineerId, id);
    }
}
