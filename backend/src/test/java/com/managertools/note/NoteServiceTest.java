package com.managertools.note;

import com.managertools.engineer.Engineer;
import com.managertools.engineer.EngineerService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NoteServiceTest {

    @Mock NoteRepository noteRepository;
    @Mock EngineerService engineerService;
    @InjectMocks NoteService noteService;

    Engineer engineer = new Engineer("Alice", "Engineer", LocalDate.now());

    @Test
    void findByEngineer_returnsNotes() {
        Note note = new Note(engineer, "Content", LocalDate.now());
        when(engineerService.findById(1L)).thenReturn(engineer);
        when(noteRepository.findByEngineerIdOrderByMeetingDateDesc(1L)).thenReturn(List.of(note));

        List<Note> result = noteService.findByEngineer(1L);

        assertThat(result).containsExactly(note);
    }

    @Test
    void findByEngineer_engineerNotFound_throws() {
        when(engineerService.findById(99L)).thenThrow(new EntityNotFoundException("Engineer not found: 99"));

        assertThatThrownBy(() -> noteService.findByEngineer(99L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void getSummary_returnsAtMostFiveNotes() {
        when(engineerService.findById(1L)).thenReturn(engineer);
        when(noteRepository.findByEngineerIdOrderByMeetingDateDesc(eq(1L), any(Pageable.class)))
                .thenReturn(List.of());

        noteService.getSummary(1L);

        verify(noteRepository).findByEngineerIdOrderByMeetingDateDesc(eq(1L), any(Pageable.class));
    }

    @Test
    void create_savesNote() {
        when(engineerService.findById(1L)).thenReturn(engineer);
        NoteRequest request = new NoteRequest("Discussed goals", LocalDate.now());
        Note saved = new Note(engineer, "Discussed goals", LocalDate.now());
        when(noteRepository.save(any())).thenReturn(saved);

        Note result = noteService.create(1L, request);

        assertThat(result.getContent()).isEqualTo("Discussed goals");
        verify(noteRepository).save(any(Note.class));
    }

    @Test
    void update_notFound_throws() {
        when(noteRepository.findById(99L)).thenReturn(Optional.empty());
        NoteRequest request = new NoteRequest("Content", LocalDate.now());

        assertThatThrownBy(() -> noteService.update(99L, request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void delete_deletesNote() {
        Note note = new Note(engineer, "Content", LocalDate.now());
        when(noteRepository.findById(1L)).thenReturn(Optional.of(note));

        noteService.delete(1L);

        verify(noteRepository).delete(note);
    }
}
