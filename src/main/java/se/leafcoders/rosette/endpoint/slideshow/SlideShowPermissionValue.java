package se.leafcoders.rosette.endpoint.slideshow;

import se.leafcoders.rosette.core.permission.PermissionValue;

public class SlideShowPermissionValue extends PermissionValue {

    private static final String ACTION_CREATE_SLIDES = "createSlides";
    private static final String ACTION_READ_SLIDES = "readSlides";
    private static final String ACTION_UPDATE_SLIDES = "updateSlides";
    private static final String ACTION_DELETE_SLIDES = "deleteSlides";

    public SlideShowPermissionValue() {
        super("slideShows");
    }

    public SlideShowPermissionValue createSlides() {
        return withAction(ACTION_CREATE_SLIDES);
    }
    
    public SlideShowPermissionValue readSlides() {
        return withAction(ACTION_READ_SLIDES);
    }
    
    public SlideShowPermissionValue updateSlides() {
        return withAction(ACTION_UPDATE_SLIDES);
    }
    
    public SlideShowPermissionValue deleteSlides() {
        return withAction(ACTION_DELETE_SLIDES);
    }
}
