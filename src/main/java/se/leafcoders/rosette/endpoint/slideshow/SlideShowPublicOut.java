package se.leafcoders.rosette.endpoint.slideshow;

import java.util.List;

import lombok.Data;

@Data
public class SlideShowPublicOut {

    private String name;
    private List<SlideOut> slides;
    
    public SlideShowPublicOut(SlideShow slideShow, List<SlideOut> slides) {
        this.name = slideShow.getName();
        this.slides = slides;
    }
}
