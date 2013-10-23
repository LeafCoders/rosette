package se.ryttargardskyrkan.rosette.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import se.ryttargardskyrkan.rosette.model.IdOrText;

/*
 * Validates that either idRef or text is set, not both. 
 */
public class HasIdOrTextValidator implements ConstraintValidator<HasIdOrText, IdOrText> {
 
    @Override
    public void initialize(HasIdOrText idOrText) {}
 
    @Override
    public boolean isValid(IdOrText idOrText, ConstraintValidatorContext cxt) {
    	if (idOrText != null) {
	    	return idOrText.hasIdRef() != idOrText.hasText();
    	}
    	return true;
    }
}
