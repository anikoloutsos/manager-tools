package io.anikoloutsos.manager_tools.feedbacknote.dto;

import java.time.Instant;
import java.util.UUID;

public record FeedbackNoteResponse(
        UUID id,
        UUID engineerId,
        Instant date,
        String giver,
        String situation,
        String task,
        String action,
        String result
) {
}
