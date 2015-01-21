package se.leafcoders.rosette.validator;

import se.leafcoders.rosette.model.BaseModel;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/*
 * Validates that ref is set and has an id
 */
public class HasRefValidator implements ConstraintValidator<HasRef, BaseModel> {
    @Override
    public void initialize(HasRef constraintAnnotation) {
    }

    @Override
    public boolean isValid(BaseModel value, ConstraintValidatorContext context) {
        return (value != null) && (value.getId() != null);
    }
}
