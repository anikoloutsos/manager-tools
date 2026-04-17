package io.anikoloutsos.manager_tools.exception;

// Abstract to enforce domain-specific subtypes (e.g. EngineerNotFoundException).
// All subtypes are automatically handled by GlobalExceptionHandler as 404.
public abstract class ResourceNotFoundException extends RuntimeException {

    protected ResourceNotFoundException(String message) {
        super(message);
    }
}
