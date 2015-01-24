package se.leafcoders.rosette.validator;

import se.leafcoders.rosette.model.reference.ObjectReferenceOrText;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/*
 * Validates that either id or text is set, not both.
 */
public class HasRefOrTextValidator implements ConstraintValidator<HasRefOrText, ObjectReferenceOrText> {
    @Override
    public void initialize(HasRefOrText constraintAnnotation) {
    }

    @Override
    public boolean isValid(ObjectReferenceOrText value, ConstraintValidatorContext context) {
        return (value != null) && (value.hasRef() != value.hasText());
    }
}
