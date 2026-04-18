package io.anikoloutsos.manager_tools.feedbacknote;

import io.anikoloutsos.manager_tools.engineer.EngineerRepository;
import io.anikoloutsos.manager_tools.exception.EngineerNotFoundException;
import io.anikoloutsos.manager_tools.exception.FeedbackNoteNotFoundException;
import io.anikoloutsos.manager_tools.feedbacknote.dto.CreateFeedbackNoteRequest;
import io.anikoloutsos.manager_tools.feedbacknote.dto.FeedbackNoteResponse;
import io.anikoloutsos.manager_tools.feedbacknote.dto.UpdateFeedbackNoteRequest;
import io.anikoloutsos.manager_tools.note.InstantToLocalDateConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class FeedbackNoteService {

    private final FeedbackNoteRepository repository;
    private final EngineerRepository engineerRepository;

    public FeedbackNoteService(FeedbackNoteRepository repository, EngineerRepository engineerRepository) {
        this.repository = repository;
        this.engineerRepository = engineerRepository;
    }

    @Transactional(readOnly = true)
    public List<FeedbackNoteResponse> findAll() {
        return repository.findAllByOrderByDateDesc().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<FeedbackNoteResponse> findAllByEngineerId(UUID engineerId) {
        return repository.findByEngineerIdOrderByDateDesc(engineerId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public FeedbackNoteResponse findById(UUID id) {
        return toResponse(repository.findById(id).orElseThrow(() -> new FeedbackNoteNotFoundException(id)));
    }

    public FeedbackNoteResponse create(CreateFeedbackNoteRequest request) {
        requireEngineerExistsIfPresent(request.engineerId());
        FeedbackNote note = new FeedbackNote(
                request.engineerId(),
                InstantToLocalDateConverter.toUtcMidnight(request.date()),
                request.giver(),
                request.situation(),
                request.task(),
                request.action(),
                request.result()
        );
        return toResponse(repository.save(note));
    }

    public void update(UUID id, UpdateFeedbackNoteRequest request) {
        FeedbackNote note = repository.findById(id)
                .orElseThrow(() -> new FeedbackNoteNotFoundException(id));
        requireEngineerExistsIfPresent(request.engineerId());
        note.setEngineerId(request.engineerId());
        note.setDate(InstantToLocalDateConverter.toUtcMidnight(request.date()));
        note.setGiver(request.giver());
        note.setSituation(request.situation());
        note.setTask(request.task());
        note.setAction(request.action());
        note.setResult(request.result());
    }

    public void delete(UUID id) {
        FeedbackNote note = repository.findById(id)
                .orElseThrow(() -> new FeedbackNoteNotFoundException(id));
        repository.delete(note);
    }

    private void requireEngineerExistsIfPresent(UUID engineerId) {
        if (engineerId != null && !engineerRepository.existsById(engineerId)) {
            throw new EngineerNotFoundException(engineerId);
        }
    }

    private FeedbackNoteResponse toResponse(FeedbackNote note) {
        return new FeedbackNoteResponse(
                note.getId(), note.getEngineerId(), note.getDate(), note.getGiver(),
                note.getSituation(), note.getTask(), note.getAction(), note.getResult()
        );
    }
}
