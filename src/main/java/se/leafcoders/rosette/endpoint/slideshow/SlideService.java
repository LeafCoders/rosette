package se.leafcoders.rosette.endpoint.slideshow;

import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import se.leafcoders.rosette.core.persistable.PersistenceService;
import se.leafcoders.rosette.endpoint.asset.AssetService;

@Service
public class SlideService extends PersistenceService<Slide, SlideIn, SlideOut> {

    @Autowired
    AssetService assetService;

    public SlideService(SlideRepository repository) {
        super(Slide.class, SlidePermissionValue::new, repository);
    }

    private SlideRepository repo() {
        return (SlideRepository) repository;
    }

    @Override
    protected Slide convertFromInDTO(SlideIn dto, JsonNode rawIn, Slide item) {
        if (rawIn == null || rawIn.has("title")) {
            item.setTitle(dto.getTitle());
        }
        if (rawIn == null || rawIn.has("duration")) {
            item.setDuration(dto.getDuration());
        }
        if (rawIn == null || rawIn.has("startTime")) {
            item.setStartTime(dto.getStartTime());
        }
        if (rawIn == null || rawIn.has("endTime")) {
            item.setEndTime(dto.getEndTime());
        }
        if (rawIn == null || rawIn.has("imageId")) {
            item.setImage(assetService.read(dto.getImageId(), true));
        }
        return item;
    }

    @Override
    protected SlideOut convertToOutDTO(Slide item) {
        SlideOut dto = new SlideOut();
        dto.setId(item.getId());
        dto.setTitle(item.getTitle());
        dto.setDuration(item.getDuration());
        dto.setStartTime(item.getStartTime());
        dto.setEndTime(item.getEndTime());
        dto.setImage(assetService.toOut(item.getImage()));
        dto.setDisplayOrder(item.getDisplayOrder());
        return dto;
    }

    public void moveSlide(Long slideShowId, Long slideId, Long toSlideId) {
        final Slide slide = read(slideId, true);
        final Slide slideMoveTo = read(toSlideId, true);
        if (slide.getDisplayOrder() < slideMoveTo.getDisplayOrder()) {
            repo().moveDisplayOrders(slideShowId, slide.getDisplayOrder(), slideMoveTo.getDisplayOrder(), -1L);
        } else {
            repo().moveDisplayOrders(slideShowId, slideMoveTo.getDisplayOrder(), slide.getDisplayOrder(), 1L);
        }
        repo().setDisplayOrder(slideId, slideMoveTo.getDisplayOrder());
    }

}
