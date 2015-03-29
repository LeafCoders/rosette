package se.leafcoders.rosette.comparator;

import java.util.Comparator;
import se.leafcoders.rosette.model.error.ValidationError;

public class ValidationErrorComparator implements Comparator<ValidationError> {
    @Override
    public int compare(ValidationError error1, ValidationError error2) {
        return error1.getMessage().compareTo(error2.getMessage());
    }
} 
