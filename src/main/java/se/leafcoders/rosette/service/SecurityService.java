
package se.ryttargardskyrkan.rosette.service;

import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import se.ryttargardskyrkan.rosette.exception.ForbiddenException;
import se.ryttargardskyrkan.rosette.exception.SimpleValidationException;
import se.ryttargardskyrkan.rosette.exception.ValidationException;
import se.ryttargardskyrkan.rosette.model.Booking;
import se.ryttargardskyrkan.rosette.model.Poster;
import se.ryttargardskyrkan.rosette.model.ValidationError;
import se.ryttargardskyrkan.rosette.model.event.Event;

@Service
public class SecurityService {

	@Autowired
	private MongoTemplate mongoTemplate;
    @Autowired
    private Validator validator;

	public boolean isPermitted(final String permission) {
		return SecurityUtils.getSubject().isPermitted(permission);
	}

	public void checkPermission(final String permission) {
		if (!SecurityUtils.getSubject().isPermitted(permission)) {
			throw new ForbiddenException("Missing permission: " + permission);
		}
	}

    public void validate(Object object) {
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(object);
        if (constraintViolations != null && !constraintViolations.isEmpty()) {
            throw new ValidationException(constraintViolations);
        }
    }

	public String requestUserId() {
		Object principal = SecurityUtils.getSubject().getPrincipal();
		if (principal != null) {
			return principal.toString();
		} else {
			return null;
		}
	}
    
	// TODO: I very ugly method. Fix with annotations? http://stackoverflow.com/a/4454783
	public void checkNotReferenced(final String id, final String permissionType) {
		if (permissionType == "locations") {
			if (mongoTemplate.exists(Query.query(Criteria.where("location.id").is(id)), Booking.class)) {
				throw new SimpleValidationException(new ValidationError("location.id", "booking.isReferencedBy"));
			}
			if (mongoTemplate.exists(Query.query(Criteria.where("location.id").is(id)), Event.class)) {
				throw new SimpleValidationException(new ValidationError("location.id", "event.isReferencedBy"));
			}
		} else if (permissionType == "uploads") {
			if (mongoTemplate.exists(Query.query(Criteria.where("image.id").is(id)), Poster.class)) {
				throw new SimpleValidationException(new ValidationError("image.id", "poster.isReferencedBy"));
			}
		}
	}
}
