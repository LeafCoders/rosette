package se.ryttargardskyrkan.rosette.model;

public class RequestError {
	private String errorMessage;
	
	public RequestError(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	
}
