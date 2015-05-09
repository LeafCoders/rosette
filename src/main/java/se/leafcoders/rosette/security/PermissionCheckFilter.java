package se.leafcoders.rosette.security;

public enum PermissionCheckFilter {

	ALL(PermissionAction.CREATE, PermissionAction.READ, PermissionAction.UPDATE, PermissionAction.DELETE),
	NONE();

	private final PermissionAction[] actionFilter;

	PermissionCheckFilter(PermissionAction... actionFilter) {
		this.actionFilter = actionFilter;
	}

	public boolean shallCheck(final PermissionAction checkAction) {
		for (PermissionAction actionType : actionFilter) {
			if (actionType == checkAction) {
				return true;
			}
		}
		return false;
	}

}
