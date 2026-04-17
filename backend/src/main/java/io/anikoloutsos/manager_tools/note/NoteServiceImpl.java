package io.anikoloutsos.manager_tools.note;

import io.anikoloutsos.manager_tools.engineer.EngineerRepository;
import io.anikoloutsos.manager_tools.exception.EngineerNotFoundException;
import io.anikoloutsos.manager_tools.exception.NoteNotFoundException;
import io.anikoloutsos.manager_tools.note.dto.CreateNoteRequest;
import io.anikoloutsos.manager_tools.note.dto.NoteResponse;
import io.anikoloutsos.manager_tools.note.dto.UpdateNoteRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;
    private final EngineerRepository engineerRepository;

    public NoteServiceImpl(NoteRepository noteRepository, EngineerRepository engineerRepository) {
        this.noteRepository = noteRepository;
        this.engineerRepository = engineerRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<NoteResponse> findAllByEngineerId(UUID engineerId) {
        if (!engineerRepository.existsById(engineerId)) {
            throw new EngineerNotFoundException(engineerId);
        }
        return noteRepository.findByEngineerIdOrderByDateDesc(engineerId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public NoteResponse findById(UUID engineerId, UUID id) {
        if (!engineerRepository.existsById(engineerId)) {
            throw new EngineerNotFoundException(engineerId);
        }
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new NoteNotFoundException(id));
        if (!note.getEngineerId().equals(engineerId)) {
            throw new NoteNotFoundException(id);
        }
        return toResponse(note);
    }

    @Override
    public NoteResponse create(UUID engineerId, CreateNoteRequest request) {
        if (!engineerRepository.existsById(engineerId)) {
            throw new EngineerNotFoundException(engineerId);
        }
        Note note = new Note(engineerId, InstantToLocalDateConverter.toUtcMidnight(request.date()), request.body());
        return toResponse(noteRepository.save(note));
    }

    @Override
    public void update(UUID engineerId, UUID id, UpdateNoteRequest request) {
        if (!engineerRepository.existsById(engineerId)) {
            throw new EngineerNotFoundException(engineerId);
        }
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new NoteNotFoundException(id));
        if (!note.getEngineerId().equals(engineerId)) {
            throw new NoteNotFoundException(id);
        }
        note.setDate(InstantToLocalDateConverter.toUtcMidnight(request.date()));
        note.setBody(request.body());
    }

    @Override
    public void delete(UUID engineerId, UUID id) {
        if (!engineerRepository.existsById(engineerId)) {
            throw new EngineerNotFoundException(engineerId);
        }
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new NoteNotFoundException(id));
        if (!note.getEngineerId().equals(engineerId)) {
            throw new NoteNotFoundException(id);
        }
        noteRepository.delete(note);
    }

    private NoteResponse toResponse(Note note) {
        return new NoteResponse(note.getId(), note.getEngineerId(), note.getDate(), note.getBody());
    }
}
