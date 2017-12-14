package se.leafcoders.rosette.persistence.validator;

import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import se.leafcoders.rosette.permission.PermissionTreeHelper;

/*
 * Validates that each permission in list is valid
 */
public class ValidPermissionsValidator implements ConstraintValidator<ValidPermissions, List<String>> {

    @Override
    public void initialize(ValidPermissions constraintAnnotation) {
    }

    @Override
    public boolean isValid(List<String> permissions, ConstraintValidatorContext context) {
        if (permissions != null) {
            for (String permission : permissions) {
                return PermissionTreeHelper.hasValidPermissionFormat(permission);
            }
        }
        return true;
    }
}
