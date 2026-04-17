package io.anikoloutsos.manager_tools.exception;

import java.util.UUID;

public class EngineerNotFoundException extends ResourceNotFoundException {

    public EngineerNotFoundException(UUID id) {
        super("Engineer not found with id: " + id);
    }
}
