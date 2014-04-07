package se.ryttargardskyrkan.rosette.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import se.ryttargardskyrkan.rosette.comparator.ValidationErrorComparator;
import se.ryttargardskyrkan.rosette.exception.ForbiddenException;
import se.ryttargardskyrkan.rosette.exception.SimpleValidationException;
import se.ryttargardskyrkan.rosette.exception.NotFoundException;
import se.ryttargardskyrkan.rosette.exception.ValidationException;
import se.ryttargardskyrkan.rosette.model.ValidationError;

@RequestMapping("v1-snapshot")
public class AbstractController {
    static final Logger logger = LoggerFactory.getLogger(AbstractController.class);

    @Autowired
    private Validator validator;

    protected boolean isPermitted(String permission) {
        return SecurityUtils.getSubject().isPermitted(permission);
    }

    protected void checkPermission(String permission) {
        if (!SecurityUtils.getSubject().isPermitted(permission)) {
            throw new ForbiddenException();
        }
    }

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
            responseBody = "Forbidden";
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
