package com.managertools.engineer;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EngineerServiceTest {

    @Mock EngineerRepository repository;
    @InjectMocks EngineerService service;

    @Test
    void findAll_delegatesToRepository() {
        Engineer e = new Engineer("Alice", "Engineer", LocalDate.now());
        when(repository.findAll()).thenReturn(List.of(e));

        List<Engineer> result = service.findAll();

        assertThat(result).containsExactly(e);
        verify(repository).findAll();
    }

    @Test
    void findById_found_returnsEngineer() {
        Engineer e = new Engineer("Alice", "Engineer", LocalDate.now());
        when(repository.findById(1L)).thenReturn(Optional.of(e));

        Engineer result = service.findById(1L);

        assertThat(result).isEqualTo(e);
    }

    @Test
    void findById_notFound_throwsEntityNotFoundException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void create_savesAndReturnsEngineer() {
        EngineerRequest request = new EngineerRequest("Alice", "Engineer", LocalDate.now());
        Engineer saved = new Engineer("Alice", "Engineer", LocalDate.now());
        when(repository.save(any())).thenReturn(saved);

        Engineer result = service.create(request);

        assertThat(result.getName()).isEqualTo("Alice");
        verify(repository).save(any(Engineer.class));
    }

    @Test
    void update_updatesFieldsAndSaves() {
        Engineer existing = new Engineer("Old Name", "Old Role", LocalDate.now());
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any())).thenReturn(existing);

        EngineerRequest request = new EngineerRequest("New Name", "New Role", LocalDate.now());
        service.update(1L, request);

        assertThat(existing.getName()).isEqualTo("New Name");
        assertThat(existing.getRole()).isEqualTo("New Role");
        verify(repository).save(existing);
    }

    @Test
    void delete_deletesExistingEngineer() {
        Engineer existing = new Engineer("Alice", "Engineer", LocalDate.now());
        when(repository.findById(1L)).thenReturn(Optional.of(existing));

        service.delete(1L);

        verify(repository).delete(existing);
    }
}
