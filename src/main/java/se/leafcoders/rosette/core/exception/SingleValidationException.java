package se.leafcoders.rosette.exception;

public class SingleValidationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private ValidationError validationError;

    public SingleValidationException() {
    }

    public SingleValidationException(ValidationError validationError) {
        super(validationError.getMessage());
        this.validationError = validationError;
    }

    public SingleValidationException(ValidationError validationError, Throwable throwable) {
        super(throwable);
        this.validationError = validationError;
    }

    public SingleValidationException(Throwable throwable) {
        super(throwable);
    }

    public ValidationError getValidationError() {
        return validationError;
    }

    public void setValidationError(ValidationError validationError) {
        this.validationError = validationError;
    }
}
