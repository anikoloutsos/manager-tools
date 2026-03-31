package com.managertools.note;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class NoteController {

    private final NoteService service;

    public NoteController(NoteService service) {
        this.service = service;
    }

    @GetMapping("/api/engineers/{engineerId}/notes")
    public List<NoteResponse> findByEngineer(@PathVariable Long engineerId) {
        return service.findByEngineer(engineerId).stream()
                .map(NoteResponse::from)
                .toList();
    }

    @GetMapping("/api/engineers/{engineerId}/notes/summary")
    public List<NoteResponse> getSummary(@PathVariable Long engineerId) {
        return service.getSummary(engineerId).stream()
                .map(NoteResponse::from)
                .toList();
    }

    @GetMapping("/api/engineers/{engineerId}/notes/search")
    public List<NoteResponse> search(@PathVariable Long engineerId, @RequestParam String q) {
        return service.search(engineerId, q).stream()
                .map(NoteResponse::from)
                .toList();
    }

    @PostMapping("/api/engineers/{engineerId}/notes")
    @ResponseStatus(HttpStatus.CREATED)
    public NoteResponse create(@PathVariable Long engineerId, @Valid @RequestBody NoteRequest request) {
        return NoteResponse.from(service.create(engineerId, request));
    }

    @PutMapping("/api/notes/{noteId}")
    public NoteResponse update(@PathVariable Long noteId, @Valid @RequestBody NoteRequest request) {
        return NoteResponse.from(service.update(noteId, request));
    }

    @DeleteMapping("/api/notes/{noteId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long noteId) {
        service.delete(noteId);
    }
}
