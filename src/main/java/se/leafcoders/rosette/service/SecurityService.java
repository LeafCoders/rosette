package se.leafcoders.rosette.service;

import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import se.leafcoders.rosette.exception.ForbiddenException;
import se.leafcoders.rosette.exception.ValidationException;
import se.leafcoders.rosette.model.Booking;
import se.leafcoders.rosette.model.Poster;
import se.leafcoders.rosette.model.event.Event;
import se.leafcoders.rosette.security.PermissionAction;
import se.leafcoders.rosette.security.PermissionType;
import util.QueryId;

@Service
public class SecurityService {

	@Autowired
	private MongoTemplate mongoTemplate;
    @Autowired
    private Validator validator;

    public String getPermissionString(PermissionType permissionType, PermissionAction permissionAction, String... params) {
		if (params.length > 0) {
			return permissionAction + ":" + permissionType + ":" + StringUtils.arrayToDelimitedString(params, ":");
		} else {
			return permissionAction + ":" + permissionType;
		}
    }
    
	public boolean isPermitted(PermissionType permissionType, PermissionAction permissionAction, String... params) {
		return SecurityUtils.getSubject().isPermitted(getPermissionString(permissionType, permissionAction, params));
	}

	public void checkPermission(PermissionType permissionType, PermissionAction permissionAction, String... params) {
		if (!isPermitted(permissionType, permissionAction, params)) {
			throwPermissionMissing(getPermissionString(permissionType, permissionAction, params));
		}
	}

	public void throwPermissionMissing(String... permissions) {
		throw new ForbiddenException("error.missingPermission",	StringUtils.arrayToCommaDelimitedString(permissions));
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
	public void checkNotReferenced(final String id, final PermissionType permissionType) {
		if (permissionType == PermissionType.LOCATIONS) {
			if (mongoTemplate.exists(Query.query(Criteria.where("location.id").is(QueryId.get(id))), Booking.class)) {
				throw new ForbiddenException("error.referencedBy", "booking");
			}
			if (mongoTemplate.exists(Query.query(Criteria.where("location.id").is(QueryId.get(id))), Event.class)) {
				throw new ForbiddenException("error.referencedBy", "event");
			}
		} else if (permissionType == PermissionType.UPLOADS) {
			if (mongoTemplate.exists(Query.query(Criteria.where("image.id").is(QueryId.get(id))), Poster.class)) {
				throw new ForbiddenException("error.referencedBy", "poster");
			}
		}
	}
}
