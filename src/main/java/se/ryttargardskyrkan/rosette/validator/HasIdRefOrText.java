package se.ryttargardskyrkan.rosette.validator;

import java.lang.annotation.Documented;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = HasIdRefOrTextValidator.class)
@Target( { METHOD, FIELD } )
@Retention(RUNTIME)
public @interface HasIdRefOrText {

	String message() default "error.hasIdRefOrText.oneMustBeSet";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
