package se.leafcoders.rosette.security;

public enum PermissionAction {
	CREATE("create"), READ("read"), UPDATE("update"), DELETE("delete");

	private final String action;

	PermissionAction(String action) {
		this.action = action;
	}

	public String toString() {
		return action;
	}

}
