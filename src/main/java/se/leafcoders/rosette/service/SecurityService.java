package se.leafcoders.rosette.service;

import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import se.leafcoders.rosette.auth.CurrentUserAuthentication;
import se.leafcoders.rosette.exception.ForbiddenException;
import se.leafcoders.rosette.exception.ValidationException;
import se.leafcoders.rosette.model.Booking;
import se.leafcoders.rosette.model.Poster;
import se.leafcoders.rosette.model.event.Event;
import se.leafcoders.rosette.security.PermissionTreeHelper;
import se.leafcoders.rosette.security.PermissionType;
import se.leafcoders.rosette.security.PermissionValue;
import util.QueryId;

@Service
public class SecurityService {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private PermissionService permissionService;

	@Autowired
    private Validator validator;

    public boolean isPermitted(PermissionValue... permissionValues) {
		for (PermissionValue value : permissionValues) {
		    if (isPermitted(value.toString())) {
				return true;
			}
		}
		return false;
	}

	public void checkPermission(PermissionValue... permissionValues) {
		if (!isPermitted(permissionValues)) {
			throwPermissionMissing(permissionValues);
		}
	}

	private boolean isPermitted(String permission) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication instanceof CurrentUserAuthentication) {
	        PermissionTreeHelper ph = new PermissionTreeHelper();
	        if (authentication.getPrincipal() != null) {
	        	ph.create(permissionService.getForUser((String)authentication.getPrincipal()));
	        } else {
	        	ph.create(permissionService.getForEveryone());
	        }
		    return PermissionTreeHelper.checkPermission(ph.getTree(), permission);
		} else {
			return false;
		}
	}
	
	public void throwPermissionMissing(PermissionValue... permissionValues) {
		throw new ForbiddenException("error.missingPermission", StringUtils.arrayToCommaDelimitedString(permissionValues));
	}

	public void validate(Object object) {
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(object);
        if (constraintViolations != null && !constraintViolations.isEmpty()) {
            throw new ValidationException(constraintViolations);
        }
    }

	public String requestUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			return (String) auth.getPrincipal();
		} else {
			return null;
		}
	}

	public void resetPermissionCache() {
	    // TODO: Clean permissionTree collection in MongoDb
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
