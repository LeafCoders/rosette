package se.leafcoders.rosette.controller.dto;

import java.util.List;
import se.leafcoders.rosette.persistence.model.SlideShow;

public class SlideShowPublicOut {

    private String name;
    private List<SlideOut> slides;
    
    public SlideShowPublicOut(SlideShow slideShow, List<SlideOut> slides) {
        this.name = slideShow.getName();
        this.slides = slides;
    }
    
    // Getters and setters
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SlideOut> getSlides() {
        return slides;
    }

    public void setSlides(List<SlideOut> slides) {
        this.slides = slides;
    }

}
