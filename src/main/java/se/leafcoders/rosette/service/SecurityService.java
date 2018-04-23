package se.leafcoders.rosette.service;

import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import se.leafcoders.rosette.auth.CurrentUserAuthentication;
import se.leafcoders.rosette.exception.MultipleValidationException;
import se.leafcoders.rosette.permission.PermissionResult;
import se.leafcoders.rosette.permission.PermissionTreeHelper;
import se.leafcoders.rosette.permission.PermissionValue;
import se.leafcoders.rosette.persistence.model.Persistable;
import se.leafcoders.rosette.persistence.service.PermissionService;

@Service
public class SecurityService {

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
        HashMap<String, Object> permissionTree;
        if (authentication instanceof CurrentUserAuthentication) {
            permissionTree = getPermissionTree((CurrentUserAuthentication) authentication);
        } else {
            permissionTree = getPermissionTree(null);
        }
        return PermissionTreeHelper.checkPermission(permissionTree, permission);
    }

    public void validate(Object object, JsonNode attrsToValidate) {
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(object);
        if (constraintViolations != null) {
            if (attrsToValidate != null) {
                constraintViolations = constraintViolations.stream().filter(cv -> attrsToValidate.has(cv.getPropertyPath().toString()))
                    .collect(Collectors.toSet());
            }
            if (!constraintViolations.isEmpty()) {
                throw new MultipleValidationException(constraintViolations);
            }
        }
    }

    public Long requestUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            if (auth.getPrincipal() instanceof Long) {
                return (Long) auth.getPrincipal();
            }
        }
        return null;
    }

    private HashMap<String, Object> getPermissionTree(CurrentUserAuthentication authentication) {
        PermissionTreeHelper pth = new PermissionTreeHelper();
        if (authentication == null) {
            pth.create(permissionService.getForPublic());
        } else {
            Long userId = (Long) authentication.getPrincipal();
            if (userId != null) {
                pth.create(permissionService.getForUser(userId));
            } else {
                pth.create(permissionService.getForEveryone());
            }
        }
        return pth.getTree();
    }

    public void resetPermissionCache() {
        // mongoTemplate.dropCollection("permissionTrees");
    }

    public void checkNotReferenced(final Long id, final Class<? extends Persistable> referenceClass) {
        // new ReferenceUsageFinder(mongoTemplate, referenceClass,
        // id).checkIsReferenced();
    }
}
