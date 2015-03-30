package se.leafcoders.rosette.validator;

import java.util.List;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.apache.shiro.authz.permission.WildcardPermission;

/*
 * Validates that each permission in list is valid
 */
public class ValidPermissionsValidator implements ConstraintValidator<ValidPermissions, List<String> > {
    @Override
    public void initialize(ValidPermissions constraintAnnotation) {
    }

    @Override
    public boolean isValid(List<String> permissions, ConstraintValidatorContext context) {
    	if (permissions != null) {
    		for (String permission : permissions) {
    			if (permission != null && !permission.isEmpty()) {
	    			try {
	    				new WildcardPermission(permission);
	    			} catch (Exception ignore) {
	    				return false;
	    			}
    			}
    		}
    	}
        return true;
    }
}
