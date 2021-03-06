package se.leafcoders.rosette.core.exception;

public class NotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String className, Long id) {
        super("Id (" + id + ") of resource type (" + className + ") was not found.");
    }

    public <T> NotFoundException(Class<T> clazz, Long id) {
        this(clazz.getSimpleName(), id);
    }

    public <T> NotFoundException(Class<T> clazz, String idAlias) {
        super("IdAlias (" + idAlias + ") of resource type (" + clazz.getSimpleName() + ") was not found.");
    }

}
