package se.leafcoders.rosette.permission;

import se.leafcoders.rosette.persistence.model.Persistable;

public class PermissionValue {
    private final PermissionType type;
    private final PermissionAction action;
    private Long id = null;
    private String part = null;
    private final String[] params;

    public PermissionValue(PermissionType type, PermissionAction action, String... params) {
        this.type = type;
        this.action = action;
        this.params = params;
    }

    public PermissionValue forPersistable(Persistable data) {
        this.id = data != null ? data.getId() : null;
        return this;
    }

    public PermissionValue forId(Long id) {
        this.id = id;
        return this;
    }

    public PermissionValue part(String part) {
        this.part = part;
        return this;
    }

    @Override
    public String toString() {
        String permission = type.withAction(action);
        if (id != null) {
            permission += ":" + id.toString();
            if (part != null) {
                permission += ":" + part;
            }
        } else if (part != null) {
            permission += ":*:" + part;

        }
        if (params != null) {
            for (String param : params) {
                if (param != null) {
                    permission += ":" + param;
                }
            }
        }
        return permission;
    }
}
