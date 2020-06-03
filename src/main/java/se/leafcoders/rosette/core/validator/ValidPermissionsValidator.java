package se.leafcoders.rosette.persistence.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import se.leafcoders.rosette.permission.PermissionTreeHelper;

/*
 * Validates that each permission in string are valid
 */
public class ValidPermissionsValidator implements ConstraintValidator<ValidPermissions, String> {

    @Override
    public void initialize(ValidPermissions constraintAnnotation) {
    }

    @Override
    public boolean isValid(String permissions, ConstraintValidatorContext context) {
        if (permissions != null) {
            for (String permission : permissions.split(PermissionTreeHelper.PERMISSION_DIVIDER)) {
                if (!PermissionTreeHelper.hasValidPermissionFormat(permission)) {
                    return false;
                }
            }
        }
        return true;
    }
}
