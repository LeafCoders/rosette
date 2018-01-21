package se.leafcoders.rosette.persistence.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import se.leafcoders.rosette.controller.dto.SlideIn;
import se.leafcoders.rosette.controller.dto.SlideOut;
import se.leafcoders.rosette.permission.PermissionType;
import se.leafcoders.rosette.persistence.model.Slide;
import se.leafcoders.rosette.persistence.repository.SlideRepository;

@Service
public class SlideService extends PersistenceService<Slide, SlideIn, SlideOut> {

    @Autowired
    AssetService assetService;

    public SlideService(SlideRepository repository) {
        super(Slide.class, PermissionType.SLIDE_SHOWS, repository);
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
        return dto;
    }
}