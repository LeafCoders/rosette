package se.leafcoders.rosette.security;

import org.springframework.util.StringUtils;

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
		if (params != null && params.length > 0) {
			return type.withAction(action) + ":" + StringUtils.arrayToDelimitedString(params, ":");
		} else {
			return type.withAction(action);
		}
	}
}
