package se.leafcoders.rosette.core.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import se.leafcoders.rosette.auth.CurrentUserAuthentication;
import se.leafcoders.rosette.core.exception.MultipleValidationException;
import se.leafcoders.rosette.core.permission.PermissionResult;
import se.leafcoders.rosette.core.permission.PermissionTree;
import se.leafcoders.rosette.core.permission.PermissionTreeHelper;
import se.leafcoders.rosette.core.permission.PermissionValue;
import se.leafcoders.rosette.core.persistable.Persistable;

@RequiredArgsConstructor
@Service
public class SecurityService {

    private final PermissionSumService permissionSumService;
    private final Validator validator;

    public void checkPermission(PermissionValue... permissionValues) {
        permissionResultFor(permissionValues).checkAndThrow();
    }

    public boolean isPermitted(PermissionValue... permissionValues) {
        return permissionResultFor(permissionValues).isPermitted();
    }

    public PermissionResult permissionResultFor(PermissionValue... permissionValues) {
        return permissionResultFor(Stream.of(permissionValues).collect(Collectors.toList()));
    }

    public PermissionResult permissionResultFor(List<PermissionValue> permissionValues) {
        for (PermissionValue value : permissionValues) {
            if (value.toStringListForEachId().stream().anyMatch(permission -> isPermitted(permission))) {
                return new PermissionResult();
            }
        }
        return new PermissionResult(permissionValues);
    }

    private boolean isPermitted(String permission) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PermissionTree permissionTree = new PermissionTree();
        if (authentication instanceof CurrentUserAuthentication) {
            PermissionTree pt = ((CurrentUserAuthentication) authentication).getPermissionTree();
            if (pt != null) {
                permissionTree = pt;
            } else {
                permissionTree = getPermissionTree((CurrentUserAuthentication) authentication);
                ((CurrentUserAuthentication) authentication).setPermissionTree(permissionTree);
            }
        } else {
            permissionTree = getPermissionTree(null);
        }
        return PermissionTreeHelper.checkPermission(permissionTree, permission);
    }

    public void validate(Object object, JsonNode attrsToValidate) {
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(object);
        if (constraintViolations != null) {
            if (attrsToValidate != null) {
                constraintViolations = constraintViolations.stream()
                        .filter(cv -> attrsToValidate.has(cv.getPropertyPath().toString())).collect(Collectors.toSet());
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

    @NonNull
    private PermissionTree getPermissionTree(CurrentUserAuthentication authentication) {
        final PermissionTreeHelper pth = new PermissionTreeHelper();
        if (authentication == null) {
            pth.create(permissionSumService.getForPublic());
        } else {
            Long userId = (Long) authentication.getPrincipal();
            if (userId != null) {
                pth.create(permissionSumService.getForUser(userId));
            } else {
                pth.create(permissionSumService.getForEveryone());
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
