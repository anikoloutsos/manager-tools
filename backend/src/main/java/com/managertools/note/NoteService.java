package com.managertools.note;

import com.managertools.engineer.Engineer;
import com.managertools.engineer.EngineerService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class NoteService {

    private static final int SUMMARY_SIZE = 5;

    private final NoteRepository noteRepository;
    private final EngineerService engineerService;

    public NoteService(NoteRepository noteRepository, EngineerService engineerService) {
        this.noteRepository = noteRepository;
        this.engineerService = engineerService;
    }

    public List<Note> findByEngineer(Long engineerId) {
        engineerService.findById(engineerId); // validate engineer exists
        return noteRepository.findByEngineerIdOrderByMeetingDateDesc(engineerId);
    }

    public List<Note> getSummary(Long engineerId) {
        engineerService.findById(engineerId);
        return noteRepository.findByEngineerIdOrderByMeetingDateDesc(
                engineerId, PageRequest.of(0, SUMMARY_SIZE));
    }

    public List<Note> search(Long engineerId, String query) {
        engineerService.findById(engineerId);
        return noteRepository.searchByContent(engineerId, query);
    }

    @Transactional
    public Note create(Long engineerId, NoteRequest request) {
        Engineer engineer = engineerService.findById(engineerId);
        Note note = new Note(engineer, request.content(), request.meetingDate());
        return noteRepository.save(note);
    }

    @Transactional
    public Note update(Long noteId, NoteRequest request) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new EntityNotFoundException("Note not found: " + noteId));
        note.setContent(request.content());
        note.setMeetingDate(request.meetingDate());
        return noteRepository.save(note);
    }

    @Transactional
    public void delete(Long noteId) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new EntityNotFoundException("Note not found: " + noteId));
        noteRepository.delete(note);
    }
}
