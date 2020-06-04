package se.leafcoders.rosette.core.permission;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import lombok.NonNull;
import se.leafcoders.rosette.core.persistable.Persistable;

public abstract class PermissionValue {

    private final String type;
    private String action = null;
    private List<Long> ids = Collections.emptyList();

    protected PermissionValue(@NonNull String type) {
        this.type = type;
    }

    @SuppressWarnings("unchecked")
    public <TYPE> TYPE withAction(@NonNull String action) {
        this.action = action;
        return (TYPE) this;
    }

    public PermissionValue action(PermissionAction action) {
        return withAction(action.toString());
    }

    public PermissionValue create() {
        return withAction(PermissionAction.CREATE.toString());
    }

    public PermissionValue read() {
        return withAction(PermissionAction.READ.toString());
    }

    public PermissionValue update() {
        return withAction(PermissionAction.UPDATE.toString());
    }

    public PermissionValue delete() {
        return withAction(PermissionAction.DELETE.toString());
    }

    public PermissionValue publicPermission() {
        return withAction(PermissionAction.PUBLIC.toString());
    }

    public PermissionValue forPersistable(Persistable persistable) {
        this.ids = persistable != null ? Collections.singletonList(persistable.getId()) : Collections.emptyList();
        return this;
    }

    public PermissionValue forPersistables(List<? extends Persistable> persistables) {
        this.ids = persistables != null ? persistables.stream().map(p -> p.getId()).collect(Collectors.toList())
                : Collections.emptyList();
        return this;
    }

    public PermissionValue forId(Long id) {
        this.ids = id != null ? Collections.singletonList(id) : Collections.emptyList();
        return this;
    }

    @Override
    public String toString() {
        String permission = type;
        if (action != null) {
            permission += ":" + action;
        }
        if (!ids.isEmpty()) {
            permission += ":" + ids.stream().map(Object::toString).collect(Collectors.joining(","));
        }
        return permission;
    }

    public List<String> toStringListForEachId() {
        final String permission = type + ":" + (action != null ? action : "*");
        if (ids.isEmpty()) {
            return Collections.singletonList(permission);
        } else {
            return ids.stream().map(id -> permission + ":" + id.toString()).collect(Collectors.toList());
        }
    }
}
