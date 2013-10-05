package se.ryttargardskyrkan.rosette.validator;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import se.ryttargardskyrkan.rosette.validator.StartEndTime;
import org.apache.commons.beanutils.BeanUtils;

/**
 * See interface documentation for {@Code StartEndTime}
 */
public class StartEndTimeValidator implements ConstraintValidator<StartEndTime, Object> {
	
	private static final String DATETIMEFORMAT = "EEE MMM dd HH:mm:ss zzzz yyyy";
	
    private String startTimeFieldName;
    private String endTimeFieldName;

    @Override
    public void initialize(final StartEndTime constraintAnnotation)
    {
        startTimeFieldName = constraintAnnotation.start();
        endTimeFieldName = constraintAnnotation.end();
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context)
    {
        try
        {
            final String startTimeString = BeanUtils.getProperty(value, startTimeFieldName);
            final String endTimeString = BeanUtils.getProperty(value, endTimeFieldName);

            // This validator does not check for null times. Use @NotNull if needed
            if ((startTimeString == null) || (endTimeString == null)) {
            	return true;
            }

            final SimpleDateFormat dateFormat = new SimpleDateFormat(DATETIMEFORMAT);
            final Date startTime = dateFormat.parse(startTimeString);
            final Date endTime = dateFormat.parse(endTimeString);
           	return startTime.before(endTime);
        }
        catch (final Exception ignore) {}
        return false;
    }
}