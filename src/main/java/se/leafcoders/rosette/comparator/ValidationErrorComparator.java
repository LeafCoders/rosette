package se.ryttargardskyrkan.rosette.comparator;

import java.util.Comparator;
import se.ryttargardskyrkan.rosette.model.ValidationError;

public class ValidationErrorComparator implements Comparator<ValidationError> {
    @Override
    public int compare(ValidationError error1, ValidationError error2) {
        return error1.getMessage().compareTo(error2.getMessage());
    }
} 
