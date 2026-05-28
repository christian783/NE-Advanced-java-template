package io.app.my_app.exception;

public class EntityNotFoundException extends ResourceNotFoundException {
    private static final long serialVersionUID = 1L;

    public EntityNotFoundException(String message, Object... args) {
        super(message, args);
    }
}