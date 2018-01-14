package se.leafcoders.rosette.exception;

import java.util.Collections;
import java.util.List;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalRequestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({ ForbiddenException.class })
    public ResponseEntity<ExceptionError> handleForbiddenException(ForbiddenException ex, WebRequest request) {
        System.err.println(ex.getMessage());
        ex.printStackTrace(System.err);
        return new ResponseEntity<ExceptionError>(
            new ExceptionError(ApiError.FORBIDDEN.toString(), ex.getReason(), ex.getReasonParams()), new HttpHeaders(), HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler({ NotFoundException.class })
    public ResponseEntity<ExceptionError> handleNotFoundException(NotFoundException ex, WebRequest request) {
        System.err.println(ex.getMessage());
        ex.printStackTrace(System.err);
        return new ResponseEntity<ExceptionError>(
            new ExceptionError(ApiError.NOT_FOUND.toString(), ex.getMessage(), null), new HttpHeaders(), HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler({ SingleValidationException.class })
    public ResponseEntity<List<ValidationError>> handleSingleValidationException(SingleValidationException ex, WebRequest request) {
        System.err.println(ex.getValidationError());
        ex.printStackTrace(System.err);
        return new ResponseEntity<List<ValidationError>>(Collections.singletonList(ex.getValidationError()), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ MultipleValidationException.class })
    public ResponseEntity<List<ValidationError>> handleMultipleValidationException(MultipleValidationException ex, WebRequest request) {
        System.err.println(ex.getValidationErrors());
        ex.printStackTrace(System.err);
        return new ResponseEntity<List<ValidationError>>(ex.getValidationErrors(), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ DataAccessException.class })
    public ResponseEntity<List<ValidationError>> handleDataAccessException(DataAccessException ex, WebRequest request) {
        System.err.println(ex.getMessage());
        ex.printStackTrace(System.err);
        
        // TODO: Handle separately in create/update and delete
        ValidationError validationError = new ValidationError("id", ApiError.UNKNOWN_REASON);
        return new ResponseEntity<List<ValidationError>>(Collections.singletonList(validationError), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ MultipartException.class })
    public ResponseEntity<List<ValidationError>> handleFileSizeLimitExceededException(MultipartException ex, WebRequest request) {
        System.err.println(ex.getMessage());
        ex.printStackTrace(System.err);
        ValidationError validationError = new ValidationError("uploading", ApiString.FILE_EXCEED_SIZE);
        return new ResponseEntity<List<ValidationError>>(Collections.singletonList(validationError), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        System.err.println(ex.getMessage());
        ex.printStackTrace(System.err);
        return super.handleExceptionInternal(ex, body, headers, status, request);
    }

}
