package se.ryttargardskyrkan.rosette.validator;

import se.ryttargardskyrkan.rosette.model.ObjectReferenceOrText;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/*
 * Validates that either idRef or text is set, not both.
 */
public class HasIdRefOrTextValidator implements ConstraintValidator<HasIdRefOrText, ObjectReferenceOrText> {
    @Override
    public void initialize(HasIdRefOrText constraintAnnotation) {
    }

    @Override
    public boolean isValid(ObjectReferenceOrText value, ConstraintValidatorContext context) {
        return (value != null) && (value.hasIdRef() != value.hasText());
    }
}
