package io.anikoloutsos.manager_tools.feedbacknote;

import io.anikoloutsos.manager_tools.feedbacknote.dto.CreateFeedbackNoteRequest;
import io.anikoloutsos.manager_tools.feedbacknote.dto.FeedbackNoteResponse;
import io.anikoloutsos.manager_tools.feedbacknote.dto.UpdateFeedbackNoteRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class FeedbackNoteController {

    private final FeedbackNoteService service;

    public FeedbackNoteController(FeedbackNoteService service) {
        this.service = service;
    }

    @GetMapping("/feedback-notes")
    public List<FeedbackNoteResponse> getAll() {
        return service.findAll();
    }

    @GetMapping("/feedback-notes/{id}")
    public FeedbackNoteResponse getById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @GetMapping("/engineers/{engineerId}/feedback-notes")
    public List<FeedbackNoteResponse> getByEngineerId(@PathVariable UUID engineerId) {
        return service.findAllByEngineerId(engineerId);
    }

    @PostMapping("/feedback-notes")
    @ResponseStatus(HttpStatus.CREATED)
    public FeedbackNoteResponse create(@RequestBody @Valid CreateFeedbackNoteRequest request) {
        return service.create(request);
    }

    @PutMapping("/feedback-notes/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable UUID id, @RequestBody @Valid UpdateFeedbackNoteRequest request) {
        service.update(id, request);
    }

    @DeleteMapping("/feedback-notes/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
