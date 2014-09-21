package se.ryttargardskyrkan.rosette.validator;

import se.ryttargardskyrkan.rosette.model.ObjectReference;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/*
 * Validates that idRef is set
 */
public class HasIdRefValidator implements ConstraintValidator<HasIdRef, ObjectReference> {
    @Override
    public void initialize(HasIdRef constraintAnnotation) {
    }

    @Override
    public boolean isValid(ObjectReference value, ConstraintValidatorContext context) {
        return (value != null) && (value.getIdRef() != null);
    }
}
