package se.leafcoders.rosette.core.validator;

import java.time.LocalDateTime;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

/*
 * Validates that a DateTime is after another DateTime
 */
public class DateTimeAfterValidator implements ConstraintValidator<DateTimeAfter, Object> {

    private String startFieldName;
    private String endFieldName;

    @Override
    public void initialize(DateTimeAfter constraintAnnotation) {
        startFieldName = constraintAnnotation.startDateTime();
        endFieldName = constraintAnnotation.endDateTime();
    }

    @Override
    public boolean isValid(final Object value, ConstraintValidatorContext context) {
        try {
            BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(value);
            LocalDateTime startDateTime = (LocalDateTime) wrapper.getPropertyValue(startFieldName);
            LocalDateTime endDateTime = (LocalDateTime) wrapper.getPropertyValue(endFieldName);
            if (startDateTime != null && endDateTime != null && startDateTime.isAfter(endDateTime)) {
                return false;
            }
        } catch (final Exception ignore) {
            return false;
        }
        return true;
    }
}
