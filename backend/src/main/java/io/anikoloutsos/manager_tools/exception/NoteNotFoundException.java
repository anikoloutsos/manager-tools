package io.anikoloutsos.manager_tools.exception;

import java.util.UUID;

public class NoteNotFoundException extends ResourceNotFoundException {

    public NoteNotFoundException(UUID id) {
        super("Note not found with id: " + id);
    }
}
