package se.ryttargardskyrkan.rosette.validator;

import java.lang.annotation.Documented;
import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Validation annotation to validate that start date is before end date.
 *
 * Usage
 * @StartEndTime(start = "startTime", end = "endTime", message = "Start time must be before end time")
 */
@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = StartEndTimeValidator.class)
@Documented
public @interface StartEndTime
{
    String message() default "{times.startBeforeEnd}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * @return The start time field
     */
    String start();

    /**
     * @return The end time field
     */
    String end();
}
