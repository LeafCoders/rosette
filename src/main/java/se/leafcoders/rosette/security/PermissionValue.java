package se.leafcoders.rosette.security;

public class PermissionValue {
	private final PermissionType type;
	private final PermissionAction action;
	private final String[] params;

	public PermissionValue(PermissionType type, PermissionAction action, String... params) {
		this.type = type;
		this.action = action;
		this.params = params;
	}

	@Override
	public String toString() {
        String permission = type.withAction(action);
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
