package se.leafcoders.rosette.permission;

import org.springframework.util.StringUtils;

import se.leafcoders.rosette.exception.ApiError;
import se.leafcoders.rosette.exception.ForbiddenException;

public class PermissionResult {

    private PermissionValue[] permissionValues = null;

    public PermissionResult() {
    }

    public PermissionResult(PermissionValue... permissionValues) {
        this.permissionValues = permissionValues;
    }

    public boolean isPermitted() {
        return permissionValues == null;
    }

    public void checkAndThrow() {
        if (!isPermitted()) {
            throw new ForbiddenException(ApiError.MISSING_PERMISSION, StringUtils.arrayToCommaDelimitedString(this.permissionValues));
        }
    }
}
