package se.leafcoders.rosette.core.validator;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import se.leafcoders.rosette.core.exception.ApiString;

@Documented
@Target({ ANNOTATION_TYPE, FIELD })
@Retention(RUNTIME)
@Constraint(validatedBy = {})
@NotEmpty(message = ApiString.STRING_NOT_EMPTY)
@Length(max = 32, message = ApiString.STRING_MAX_32_CHARS)
@Pattern(regexp = "[a-z][a-zA-Z0-9]+", message = ApiString.IDALIAS_INVALID_FORMAT)
public @interface IdAlias {

    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
