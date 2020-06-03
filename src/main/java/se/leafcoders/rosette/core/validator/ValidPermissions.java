package se.leafcoders.rosette.persistence.validator;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

import se.leafcoders.rosette.exception.ApiString;

@Documented
@Constraint(validatedBy = ValidPermissionsValidator.class)
@Target({ METHOD, FIELD })
@Retention(RUNTIME)
public @interface ValidPermissions {

    String message() default ApiString.PERMISSIONS_INVALID;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
