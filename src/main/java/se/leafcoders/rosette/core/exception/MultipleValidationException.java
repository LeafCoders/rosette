package se.leafcoders.rosette.core.exception;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import se.leafcoders.rosette.core.comparator.ValidationErrorComparator;

public class MultipleValidationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final Set<ConstraintViolation<Object>> constraintViolations;

    public MultipleValidationException(Set<ConstraintViolation<Object>> constraintViolations) {
        this.constraintViolations = constraintViolations;
    }

    public List<ValidationError> getValidationErrors() {
        List<ValidationError> errors = new ArrayList<ValidationError>();
        for (ConstraintViolation<Object> validationError : constraintViolations) {
            // For class validation annotation we use "errorAt" value to define
            // where the error should be placed
            String propertyName = (String) validationError.getConstraintDescriptor().getAttributes().get("errorAt");
            if (propertyName == null) {
                propertyName = validationError.getPropertyPath().toString();
            }
            errors.add(new ValidationError(propertyName, validationError.getMessage()));
        }
        // Sort all errors by property name
        Collections.sort(errors, new ValidationErrorComparator());
        return errors;
    }

    public String toString() {
        return getValidationErrors().stream().map(error -> error.getProperty() + ":" + error.getMessage())
                .collect(Collectors.joining(", "));
    }
}
