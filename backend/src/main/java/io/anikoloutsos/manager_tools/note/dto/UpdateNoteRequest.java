package io.anikoloutsos.manager_tools.note.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record UpdateNoteRequest(
        @NotNull Instant date,
        @NotBlank String body
) {
}
