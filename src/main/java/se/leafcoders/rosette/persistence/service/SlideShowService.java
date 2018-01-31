package se.leafcoders.rosette.persistence.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import se.leafcoders.rosette.controller.dto.SlideIn;
import se.leafcoders.rosette.controller.dto.SlideShowIn;
import se.leafcoders.rosette.controller.dto.SlideShowOut;
import se.leafcoders.rosette.permission.PermissionType;
import se.leafcoders.rosette.persistence.model.Slide;
import se.leafcoders.rosette.persistence.model.SlideShow;
import se.leafcoders.rosette.persistence.repository.SlideRepository;
import se.leafcoders.rosette.persistence.repository.SlideShowRepository;

@Service
public class SlideShowService extends PersistenceService<SlideShow, SlideShowIn, SlideShowOut> {

    @Autowired
    private AssetFolderService assetFolderService;

    private class SlideCrud extends ChildCrud<SlideShow, Slide, SlideIn> {

        public SlideCrud(SlideShowService service, SlideService slideService, SlideRepository slideRepository) {
            super(service, SlideShow.class, slideService, slideRepository, Slide.class, "slides");
        }

        @Override
        public Long getParentId(Slide child) {
            return child.getSlideShowId();
        }

        @Override
        public List<Slide> getChildren(SlideShow parent) {
            return parent.getSlides();
        }

        @Override
        public void addChild(SlideShow parent, Slide child) {
            parent.addSlide(child);
        }
    };

    private SlideCrud slideCrud;
    private SlideService slideService;

    public SlideShowService(SlideShowRepository repository, SlideService slideService, SlideRepository slideRepository) {
        super(SlideShow.class, PermissionType.SLIDE_SHOWS, repository);
        slideCrud = new SlideCrud(this, slideService, slideRepository);
        this.slideService = slideService;
    }

    @Override
    protected SlideShow convertFromInDTO(SlideShowIn dto, JsonNode rawIn, SlideShow item) {
        if (rawIn == null || rawIn.has("idAlias")) {
            item.setIdAlias(dto.getIdAlias());
        }
        if (rawIn == null || rawIn.has("name")) {
            item.setName(dto.getName());
        }
        if (rawIn == null || rawIn.has("assetFolderId")) {
            item.setAssetFolder(assetFolderService.read(dto.getAssetFolderId(), true));
        }
        return item;
    }

    @Override
    protected SlideShowOut convertToOutDTO(SlideShow item) {
        SlideShowOut dto = new SlideShowOut();
        dto.setId(item.getId());
        dto.setIdAlias(item.getIdAlias());
        dto.setName(item.getName());
        dto.setAssetFolder(assetFolderService.toOut(item.getAssetFolder()));
        dto.setSlides(item.getSlides().stream().map(slide -> slideService.toOut(slide)).collect(Collectors.toList()));
        return dto;
    }

    public List<Slide> readSlides(Long slideShowId) {
        return slideCrud.readAll(slideShowId);
    }

    public Slide addSlide(Long slideShowId, SlideIn slide) {
        return slideCrud.add(slideShowId, slide);
    }

    public Slide updateSlide(Long slideShowId, Long slideId, HttpServletRequest request) {
        return slideCrud.update(slideShowId, slideId, SlideIn.class, request);
    }

    public ResponseEntity<Void> deleteSlide(Long slideShowId, Long slideId) {
        return slideCrud.delete(slideShowId, slideId);
    }
}
