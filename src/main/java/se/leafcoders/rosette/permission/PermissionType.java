package se.leafcoders.rosette.permission;

public enum PermissionType {

    ADMIN_DB("admin:db"),
    ARTICLES("articles"),
    ARTICLE_SERIES("articleSeries"),
    ARTICLE_TYPES("articleTypes"),
    ASSETS("assets"),
    ASSET_FOLDERS("assetFolders"),
    ASSET_FOLDERS_FILES("assetFolders:files"),
    EDUCATIONS("educations"),
    EDUCATIONS_EDUCATION_TYPES("educations:educationTypes"),
    EDUCATION_THEMES("educationThemes"),
    EDUCATION_THEMES_EDUCATION_TYPES("educationThemes:educationTypes"),
    EDUCATION_TYPES("educationTypes"),
    EVENTS("events"),
    EVENTS_BY_EVENT_TYPES("eventsByEventTypes"),
    EVENT_TYPES("eventTypes"),
    GROUPS("groups"),
    MESSAGES("messages"),
    PERMISSIONS("permissions"),
    PODCASTS("podcasts"),
    RESOURCES("resources"),
    RESOURCE_TYPES("resourceTypes"),
    SLIDE_SHOWS("slideShows"),
    TEXT_VALUES("textValues"),
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
