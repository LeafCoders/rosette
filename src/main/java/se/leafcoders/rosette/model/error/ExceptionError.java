package se.leafcoders.rosette.model.error;

public class ExceptionError {
	private String error;
	private String reason;
	private String[] reasonParams;
	
	public ExceptionError(String error, String reason, String[] reasonParams) {
		this.error = error;
		this.reason = reason;
		this.reasonParams = reasonParams;
	}

	public String getError() {
		return error;
	}
	
	public void setError(String error) {
		this.error = error;
	}
	
	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String[] getReasonParams() {
		return reasonParams;
	}

	public void setReasonParams(String[] reasonParams) {
		this.reasonParams = reasonParams;
	}
}
