package se.leafcoders.rosette.validator;

import java.lang.annotation.Documented;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = HasRefValidator.class)
@Target( { METHOD, FIELD } )
@Retention(RUNTIME)
public @interface HasRef {

	String message() default "error.hasRef.idMustBeSet";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
