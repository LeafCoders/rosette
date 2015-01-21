package se.ryttargardskyrkan.rosette.exception;

import java.util.Set;

import javax.validation.ConstraintViolation;

public class ValidationException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	private final Set<ConstraintViolation<Object>> constraintViolations;

	public ValidationException(Set<ConstraintViolation<Object>> constraintViolations) {
		this.constraintViolations = constraintViolations;
	}

	public Set<ConstraintViolation<Object>> getConstraintViolations() {
		return constraintViolations;
	}
}
