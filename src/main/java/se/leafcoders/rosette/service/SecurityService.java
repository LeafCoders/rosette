package se.leafcoders.rosette.service;

import java.util.HashMap;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import se.leafcoders.rosette.auth.CurrentUserAuthentication;
import se.leafcoders.rosette.exception.ValidationException;
import se.leafcoders.rosette.model.BaseModel;
import se.leafcoders.rosette.model.PermissionTree;
import se.leafcoders.rosette.security.PermissionResult;
import se.leafcoders.rosette.security.PermissionTreeHelper;
import se.leafcoders.rosette.security.PermissionValue;
import se.leafcoders.rosette.util.ReferenceUsageFinder;

@Service
public class SecurityService {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private PermissionService permissionService;

	@Autowired
    private Validator validator;

    public void checkPermission(PermissionValue... permissionValues) {
        permissionResultFor(permissionValues).checkAndThrow();
    }

    public boolean isPermitted(PermissionValue... permissionValues) {
        return permissionResultFor(permissionValues).isPermitted();
    }

	public PermissionResult permissionResultFor(PermissionValue... permissionValues) {
        for (PermissionValue value : permissionValues) {
            if (isPermitted(value.toString())) {
                return new PermissionResult();
            }
        }
        return new PermissionResult(permissionValues);
	}

	private boolean isPermitted(String permission) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication instanceof CurrentUserAuthentication) {
			HashMap<String, Object> permissionTree = getPermissionTree((CurrentUserAuthentication) authentication);
		    return PermissionTreeHelper.checkPermission(permissionTree, permission);
		} else {
			return false;
		}
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

	private HashMap<String, Object> getPermissionTree(CurrentUserAuthentication authentication) {
		String userId = (String) authentication.getPrincipal();
		if (userId == null) {
			userId = "000aaa000000a000aaa00000";
		}

		PermissionTree permissionTree = mongoTemplate.findById(userId, PermissionTree.class);		
		if (permissionTree != null) {
			return permissionTree.getTree();
		}

        PermissionTreeHelper pth = new PermissionTreeHelper();
        if (authentication.getPrincipal() != null) {
        	pth.create(permissionService.getForUser(userId));
        } else {
        	pth.create(permissionService.getForEveryone());
        }

        permissionTree = new PermissionTree();
        permissionTree.setId(userId);
        permissionTree.setTree(pth.getTree());
        mongoTemplate.save(permissionTree);

        return pth.getTree();
	}
	
	public void resetPermissionCache() {
		mongoTemplate.dropCollection("permissionTrees");
	}
	
	public void checkNotReferenced(final String id, final Class<? extends BaseModel> referenceClass) {
	    new ReferenceUsageFinder(mongoTemplate, referenceClass, id).checkIsReferenced();
	}
}
