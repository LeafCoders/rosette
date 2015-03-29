package se.leafcoders.rosette.exception;

public class ForbiddenException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private String[] reasonParams;

	public ForbiddenException(String reason, String... reasonParams) {
		super(reason);
		this.setReasonParams(reasonParams);
	}

	public String[] getReasonParams() {
		return reasonParams;
	}

	public void setReasonParams(String[] reasonParams) {
		this.reasonParams = reasonParams;
	}
}
