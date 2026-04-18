package io.anikoloutsos.manager_tools.feedbacknote.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public record UpdateFeedbackNoteRequest(
        UUID engineerId,
        @NotNull Instant date,
        String giver,
        String situation,
        String task,
        String action,
        String result
) {

    @JsonIgnore
    @AssertTrue(message = "at least one of situation, task, action, result must be present")
    public boolean isAtLeastOneStarFieldPresent() {
        return hasText(situation) || hasText(task) || hasText(action) || hasText(result);
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
