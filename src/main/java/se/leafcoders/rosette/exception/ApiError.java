package se.leafcoders.rosette.exception;

public enum ApiError {

    // Errors
    UNKNOWN_ERROR("error.unknown"),

    FORBIDDEN("error.forbidden"),

    NOT_FOUND("error.notFound"),

    AUTH_INCORRECT_PASSWORD("auth.incorrectPassword"),

    AUTH_USER_NOT_FOUND("auth.userNotFound"),

    AUTH_USER_NOT_ACTIVATED("auth.userNotActivated"),

    AUTH_USER_IS_SUPER_ADMIN("auth.userIsSuperAdmin"),

    // Reasons
    UNKNOWN_REASON("reason.unknown"),

    MISSING_PERMISSION("reason.missingPermission"),

    CHILD_ALREADY_EXIST("reason.childAlreadyExist"),

    CHILD_DONT_BELONG_TO("reason.childDontBelongTo"),

    CREATE_ALREADY_EXIST("reason.create.alreadyExist"),
    ;

    private String langCode;

    ApiError(String langCode) {
        this.langCode = langCode;
    }

    public String toString() {
        return langCode;
    }
}
