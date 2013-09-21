package se.ryttargardskyrkan.rosette.exception;

import se.ryttargardskyrkan.rosette.model.ValidationError;

public class SimpleValidationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private ValidationError validationError;

    public SimpleValidationException() {
    }

    public SimpleValidationException(ValidationError validationError) {
        this.validationError = validationError;
    }

    public SimpleValidationException(ValidationError validationError, Throwable throwable) {
        super(throwable);
        this.validationError = validationError;
    }

    public SimpleValidationException(Throwable throwable) {
        super(throwable);
    }

    public ValidationError getValidationError() {
        return validationError;
    }

    public void setValidationError(ValidationError validationError) {
        this.validationError = validationError;
    }
}
