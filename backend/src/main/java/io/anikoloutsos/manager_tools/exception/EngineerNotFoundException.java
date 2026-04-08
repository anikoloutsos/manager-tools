package io.anikoloutsos.manager_tools.exception;

import java.util.UUID;

public class EngineerNotFoundException extends RuntimeException {

    public EngineerNotFoundException(UUID id) {
        super("Engineer not found with id: " + id);
    }
}
