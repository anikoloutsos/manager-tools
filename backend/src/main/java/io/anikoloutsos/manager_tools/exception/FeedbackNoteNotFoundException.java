package io.anikoloutsos.manager_tools.exception;

import java.util.UUID;

public class FeedbackNoteNotFoundException extends ResourceNotFoundException {

    public FeedbackNoteNotFoundException(UUID id) {
        super("Feedback note not found with id: " + id);
    }
}
