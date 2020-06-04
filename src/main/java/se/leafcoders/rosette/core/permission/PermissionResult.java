package se.leafcoders.rosette.core.permission;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import se.leafcoders.rosette.core.exception.ApiError;
import se.leafcoders.rosette.core.exception.ForbiddenException;

public class PermissionResult {

    private List<PermissionValue> permissionValues = null;

    public PermissionResult() {
    }

    public PermissionResult(List<PermissionValue> permissionValues) {
        this.permissionValues = permissionValues;
    }

    public PermissionResult(PermissionValue... permissionValues) {
        this.permissionValues = Stream.of(permissionValues).collect(Collectors.toList());
    }

    public boolean isPermitted() {
        return permissionValues == null || permissionValues.isEmpty();
    }

    public void checkAndThrow() {
        if (!isPermitted()) {
            throw new ForbiddenException(ApiError.MISSING_PERMISSION,
                    permissionValues.stream().map(PermissionValue::toString).collect(Collectors.joining(", ")));
        }
    }
}
