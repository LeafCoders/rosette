package se.leafcoders.rosette.permission;

public class PermissionValueSlideShows extends PermissionValue {

    private static final String ACTION_CREATE_SLIDES = "createSlides";
    private static final String ACTION_READ_SLIDES = "readSlides";
    private static final String ACTION_UPDATE_SLIDES = "updateSlides";
    private static final String ACTION_DELETE_SLIDES = "deleteSlides";

    public PermissionValueSlideShows() {
        super("slideShows");
    }

    public PermissionValueSlideShows createSlides() {
        return withAction(ACTION_CREATE_SLIDES);
    }
    
    public PermissionValueSlideShows readSlides() {
        return withAction(ACTION_READ_SLIDES);
    }
    
    public PermissionValueSlideShows updateSlides() {
        return withAction(ACTION_UPDATE_SLIDES);
    }
    
    public PermissionValueSlideShows deleteSlides() {
        return withAction(ACTION_DELETE_SLIDES);
    }
}
