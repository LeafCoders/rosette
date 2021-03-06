package se.leafcoders.rosette.core.comparator;

import java.util.Comparator;

import se.leafcoders.rosette.core.exception.ValidationError;

public class ValidationErrorComparator implements Comparator<ValidationError> {

    @Override
    public int compare(ValidationError error1, ValidationError error2) {
        return error1.getProperty().compareTo(error2.getProperty());
    }
}
