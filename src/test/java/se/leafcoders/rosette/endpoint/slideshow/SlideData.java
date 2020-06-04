package se.leafcoders.rosette.endpoint.slideshow;

import java.time.LocalDateTime;

import se.leafcoders.rosette.endpoint.asset.Asset;
import se.leafcoders.rosette.test.TimeRange;

public class SlideData {

    public static Slide imageSlide(Asset image) {
        Slide slide = new Slide();
        slide.setTitle("Image slide");
        slide.setStartTime(LocalDateTime.now());
        slide.setEndTime(LocalDateTime.now().plusHours(2));
        slide.setDuration(10);
        slide.setImage(image);
        slide.setDisplayOrder(0L);
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
