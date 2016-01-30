package se.leafcoders.rosette.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import se.leafcoders.rosette.comparator.ValidationErrorComparator;
import se.leafcoders.rosette.exception.ForbiddenException;
import se.leafcoders.rosette.exception.NotFoundException;
import se.leafcoders.rosette.exception.SimpleValidationException;
import se.leafcoders.rosette.exception.ValidationException;
import se.leafcoders.rosette.model.error.ExceptionError;
import se.leafcoders.rosette.model.error.ValidationError;

@RequestMapping("api/v1")
@CrossOrigin
public class AbstractController {
    static final Logger logger = LoggerFactory.getLogger(AbstractController.class);

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Object handleApplicationExceptions(Throwable exception, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");

        if (exception instanceof ForbiddenException) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            logger.debug("Controller exception", exception);
            
            ForbiddenException fe = (ForbiddenException) exception;
            return new ExceptionError("error.forbidden", fe.getReason(), fe.getReasonParams());
        }
        
        else if (exception instanceof NotFoundException) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            logger.debug("Controller exception", exception);

            return new ExceptionError("error.notFound", exception.getMessage(), null);
        }
        
        else if (exception instanceof ValidationException) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            logger.debug("Controller exception", exception);

            ValidationException validationException = (ValidationException) exception;
            List<ValidationError> errors = new ArrayList<ValidationError>();
            for (ConstraintViolation<Object> constraintViolation : validationException.getConstraintViolations()) {
        		errors.add(new ValidationError(constraintViolation.getPropertyPath().toString(), constraintViolation.getMessage()));
            }
            // Sort all errors by message text
            Collections.sort(errors, new ValidationErrorComparator());
            return errors;
        }
        
        else if (exception instanceof SimpleValidationException) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            logger.debug("Controller exception", exception);

            SimpleValidationException simpleValidationException = (SimpleValidationException) exception;
            List<ValidationError> errors = new ArrayList<ValidationError>();
            errors.add(simpleValidationException.getValidationError());
            return errors;
        }
        
        else if (exception instanceof HttpMessageNotReadableException) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            logger.debug("Controller exception", exception);

            return new ExceptionError("error.badRequest", exception.getMessage(), null);
        }
        
        else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            logger.error("Controller exception", exception);

            return new ExceptionError("error.unknownError", null, null);
        }
    }
}
