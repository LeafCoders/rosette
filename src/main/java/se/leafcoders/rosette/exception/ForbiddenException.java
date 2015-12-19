package se.leafcoders.rosette.exception;

public class ForbiddenException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private String reason;
	private String[] reasonParams;

	public ForbiddenException(String reason, String... reasonParams) {
		super(reason + ": " + String.join(", ", reasonParams));
		this.reason = reason;
		this.reasonParams = reasonParams;
	}

    public String getReason() {
        return reason;
    }

	public String[] getReasonParams() {
		return reasonParams;
	}
}
