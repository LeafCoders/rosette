package se.leafcoders.rosette.model;

public class ValidationError {
	private String property;
	private String message;
	
	public ValidationError(String property, String message) {
		super();
		this.property = property;
		this.message = message;
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
