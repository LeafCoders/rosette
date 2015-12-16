package se.leafcoders.rosette.exception;


public class NotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public NotFoundException(String message) {
	    super(message);
	}

	public NotFoundException(String className, String id) {
        super("Id (" + id + ") of resource type (" + className + ") was not found.");
    }
}
