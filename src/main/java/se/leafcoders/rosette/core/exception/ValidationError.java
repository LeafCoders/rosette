package se.leafcoders.rosette.exception;

public class ValidationError {
    private String property;
    private String message;

    public ValidationError(String property, String message) {
        this.property = property;
        this.message = message;
    }

    public ValidationError(String property, ApiError error) {
        this.property = property;
        this.message = error.toString();
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
