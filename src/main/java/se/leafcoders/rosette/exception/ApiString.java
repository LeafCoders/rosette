package se.leafcoders.rosette.exception;

public class ApiString {

    public static final String NOT_NULL = "error.notNull";

    public static final String STRING_NOT_EMPTY = "error.string.notEmpty";
    public static final String STRING_MAX_32_CHARS = "error.string.max32Chars";
    public static final String STRING_MAX_200_CHARS = "error.string.max200Chars";
    public static final String STRING_MAX_4000_CHARS = "error.string.max4000Chars";
    public static final String STRING_MAX_10000_CHARS = "error.string.max10000Chars";
    public static final String STRING_NOT_ANY_OF = "error.string.notAnyOf";

    public static final String NUMBER_OUT_OF_RANGE = "error.number.outOfRange";

    public static final String EMAIL_INVALID = "error.email.invalid";
    public static final String EMAIL_NOT_UNIQUE = "error.email.notUnique";

    public static final String FILENAME_INVALID = "error.filename.invalid";
    public static final String FILENAME_NOT_UNIQUE = "error.filename.notUnique";

    public static final String FILE_EXCEED_SIZE = "error.file.exceedSize";
    public static final String FILE_NOT_READABLE = "error.file.notReadable";
    public static final String FILE_INVALID_CONTENT = "error.file.invalidContent";
    public static final String FILE_MIMETYPE_NOT_ALLOWED = "error.file.mimeTypeNotAllowed";
    
    public static final String IDALIAS_INVALID_FORMAT = "error.idAlias.invalidFormat";

    public static final String PERMISSIONS_INVALID = "error.permssions.invalid";

    public static final String DURATION_TOO_SHORT = "error.duration.tooShort";

    public static final String DATE_MUST_BE_AFTER = "error.date.mustBeAfter";
    public static final String DATETIME_MUST_BE_AFTER = "error.dateTime.mustBeAfter";

}
