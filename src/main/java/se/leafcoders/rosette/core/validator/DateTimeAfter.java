package se.leafcoders.rosette.core.validator;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import se.leafcoders.rosette.core.exception.ApiString;

@Documented
@Constraint(validatedBy = DateTimeAfterValidator.class)
@Target({ TYPE, ANNOTATION_TYPE })
@Retention(RUNTIME)
public @interface DateTimeAfter {

    String message() default ApiString.DATETIME_MUST_BE_AFTER;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String startDateTime();

    String endDateTime();

    String errorAt();
}
