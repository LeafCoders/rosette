package se.leafcoders.rosette.security;

public enum PermissionType {
	
	ADMIN_DB("admin:db"),
	ASSETS("assets"),
	BOOKINGS("bookings"),
    EDUCATIONS("educations"),
    EDUCATION_THEMES("educationThemes"),
    EDUCATION_TYPES("educationTypes"),
	EVENTS("events"),
	EVENTS_EVENTTYPES("events:eventTypes"),
	EVENTS_RESOURCETYPES("events:resourceTypes"),
	EVENT_TYPES("eventTypes"),
	EVENT_WEEKS("eventWeeks"),
	GROUP_MEMBERSHIPS("groupMemberships"),
	GROUPS("groups"),
	LOCATIONS("locations"),
	PERMISSIONS("permissions"),
    PODCASTS("podcasts"),
	POSTERS("posters"),
	RESOURCE_TYPES("resourceTypes"),
	SIGNUP_USERS("signupUsers"),
	UPLOAD_FOLDERS("uploadFolders"),
	UPLOADS("uploads"),
	USERS("users"),
	PUBLIC("public");

	private final String type;

	PermissionType(String type) {
		this.type = type;
	}

	public String toString() {
		return type;
	}

	public String withAction(final PermissionAction action) {
		if (type.contains(":")) {
			return type.replaceFirst(":", ":" + action + ":");
		} else {
			return type + ":" + action;
		}
	}

}
