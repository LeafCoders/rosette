package se.leafcoders.rosette.exception;

public class ForbiddenException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private ApiError reason;
    private String[] reasonParams;

    public ForbiddenException(ApiError reason, String... reasonParams) {
        super(reason + ": " + String.join(", ", reasonParams));
        this.reason = reason;
        this.reasonParams = reasonParams;
    }

    public String getReason() {
        return reason.toString();
    }

    public String[] getReasonParams() {
        return reasonParams;
    }

    public static <C, P> ForbiddenException dontBelongsTo(Class<C> childClass, Long childId, Class<P> parentClass, Long parentId) {
        return new ForbiddenException(
            ApiError.CHILD_DONT_BELONG_TO, childClass.getSimpleName(), childId.toString(), parentClass.getSimpleName(), parentId.toString()
        );
    }
}
