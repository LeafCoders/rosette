package se.leafcoders.rosette.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import se.leafcoders.rosette.comparator.ValidationErrorComparator;
import se.leafcoders.rosette.exception.ForbiddenException;
import se.leafcoders.rosette.exception.NotFoundException;
import se.leafcoders.rosette.exception.SimpleValidationException;
import se.leafcoders.rosette.exception.ValidationException;
import se.leafcoders.rosette.model.ValidationError;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: larsa
 * Date: 2013-09-13
 * Time: 11:43
 * To change this template use File | Settings | File Templates.
 */

@ControllerAdvice
public class RestExceptionHandler {
    static final Logger logger = LoggerFactory.getLogger(RestExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Object handleApplicationExceptions(Throwable exception,
                                              HttpServletResponse response) throws IOException {

        // Default response
        Object responseBody = "Internal server error";
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.setContentType("application/json;charset=UTF-8");

        if (exception instanceof ForbiddenException) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            responseBody = "Forbidden: " + exception.getMessage();
        } else if (exception instanceof NotFoundException) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            responseBody = "Not found";
        } else if (exception instanceof ValidationException) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            ValidationException validationException = (ValidationException) exception;
            List<ValidationError> errors = new ArrayList<ValidationError>();
            for (ConstraintViolation<Object> constraintViolation : validationException.getConstraintViolations()) {
        		errors.add(new ValidationError(constraintViolation.getPropertyPath().toString(), constraintViolation.getMessage()));
            }
            // Sort all errors by message text
            Collections.sort(errors, new ValidationErrorComparator());
            responseBody = errors;
        } else if (exception instanceof SimpleValidationException) {
            SimpleValidationException simpleValidationException = (SimpleValidationException) exception;

            List<ValidationError> errors = new ArrayList<ValidationError>();
            errors.add(simpleValidationException.getValidationError());

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseBody = errors;
        } else if (exception instanceof HttpMessageNotReadableException) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseBody = "Bad request";
        }

        logger.error("Error", exception);
        return responseBody;
    }
}
