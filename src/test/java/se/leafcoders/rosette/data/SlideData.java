package se.leafcoders.rosette.data;

import java.time.LocalDateTime;

import se.leafcoders.rosette.TimeRange;
import se.leafcoders.rosette.controller.dto.SlideIn;
import se.leafcoders.rosette.persistence.model.Asset;
import se.leafcoders.rosette.persistence.model.Slide;

public class SlideData {

    public static Slide imageSlide(Asset image) {
        Slide slide = new Slide();
        slide.setTitle("Image slide");
        slide.setStartTime(LocalDateTime.now());
        slide.setEndTime(LocalDateTime.now().plusHours(2));
        slide.setDuration(10);
        slide.setImage(image);
        return slide;
    }

    public static SlideIn missingAllProperties() {
        return new SlideIn();
    }

    public static SlideIn invalidProperties(Asset image) {
        SlideIn slide = new SlideIn();
        slide.setTitle("");
        slide.setStartTime(LocalDateTime.now());
        slide.setEndTime(LocalDateTime.now().minusHours(2));
        slide.setDuration(0);
        slide.setImageId(image.getId());
        return slide;
    }

    public static SlideIn newSlide(Asset image, TimeRange timeRange) {
        SlideIn slide = new SlideIn();
        slide.setTitle("Image slide");
        slide.setStartTime(timeRange.getStart());
        slide.setEndTime(timeRange.getEnd());
        slide.setDuration(5);
        slide.setImageId(image.getId());
        return slide;
    }
}
