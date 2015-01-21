package se.leafcoders.rosette.exception;


public class ForbiddenException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ForbiddenException(String reason) {
		super(reason);
	}
}
