package se.ryttargardskyrkan.rosette.validator;

import se.ryttargardskyrkan.rosette.model.ObjectReference;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/*
 * Validates that either idRef or text is set, not both.
 */
public class HasIdRefOrTextValidator implements ConstraintValidator<HasIdRefOrText, ObjectReference> {
    @Override
    public void initialize(HasIdRefOrText constraintAnnotation) {
    }

    @Override
    public boolean isValid(ObjectReference value, ConstraintValidatorContext context) {
        if (value != null) {
            return value.hasIdRef() != value.hasText();
        }
        return true;
    }
}
