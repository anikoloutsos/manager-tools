package io.anikoloutsos.manager_tools.note.dto;

import java.time.Instant;
import java.util.UUID;

public record NoteResponse(
        UUID id,
        UUID engineerId,
        Instant date,
        String body
) {
}
