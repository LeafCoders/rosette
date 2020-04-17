package se.leafcoders.rosette.controller.dto;

import java.util.List;

import lombok.Data;
import se.leafcoders.rosette.persistence.model.SlideShow;

@Data
public class SlideShowPublicOut {

    private String name;
    private List<SlideOut> slides;
    
    public SlideShowPublicOut(SlideShow slideShow, List<SlideOut> slides) {
        this.name = slideShow.getName();
        this.slides = slides;
    }
}
