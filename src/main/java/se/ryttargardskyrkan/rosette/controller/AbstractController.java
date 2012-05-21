package se.ryttargardskyrkan.rosette.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import se.ryttargardskyrkan.rosette.exception.ForbiddenException;
import se.ryttargardskyrkan.rosette.exception.NotFoundException;
import se.ryttargardskyrkan.rosette.exception.ValidationException;
import se.ryttargardskyrkan.rosette.model.ValidationError;

@RequestMapping("v1-snapshot")
public abstract class AbstractController {
	@Autowired
    private Validator validator;

	protected void checkPermission(String permission) {
//		if (!SecurityUtils.getSubject().isPermitted(permission)) {
//			throw new ForbiddenException();
//		}
	}
	
	protected void validate(Object object) {
		Set<ConstraintViolation<Object>> constraintViolations = validator.validate(object);
		
		if (constraintViolations != null && !constraintViolations.isEmpty()) {
			throw new ValidationException(constraintViolations);
		}
	}
	
	@ExceptionHandler(Exception.class) 
	@ResponseBody 
	public Object handleApplicationExceptions(Throwable exception,
			HttpServletResponse response) throws IOException {
		
		// Default response
		Object responseBody = "Internal server error";
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		response.setContentType("application/json;charset=utf-8");
		
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
				ValidationError validationError = new ValidationError(constraintViolation.getPropertyPath().toString(), constraintViolation.getMessage());
				errors.add(validationError);
			}
			responseBody = errors;
		}
		return responseBody;
	}

}
