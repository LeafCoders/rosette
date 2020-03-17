package se.leafcoders.rosette.persistence.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import se.leafcoders.rosette.controller.dto.SlideIn;
import se.leafcoders.rosette.controller.dto.SlideShowIn;
import se.leafcoders.rosette.controller.dto.SlideShowOut;
import se.leafcoders.rosette.permission.PermissionAction;
import se.leafcoders.rosette.permission.PermissionType;
import se.leafcoders.rosette.permission.PermissionValue;
import se.leafcoders.rosette.persistence.model.Slide;
import se.leafcoders.rosette.persistence.model.SlideShow;
import se.leafcoders.rosette.persistence.repository.SlideRepository;
import se.leafcoders.rosette.persistence.repository.SlideShowRepository;

@Service
public class SlideShowService extends PersistenceService<SlideShow, SlideShowIn, SlideShowOut> {

    @Autowired
    private AssetFolderService assetFolderService;

    private class SlideCrud extends ChildCrud<SlideShow, Slide, SlideIn> {

        private final SlideRepository slideRepository;
        
        public SlideCrud(SlideShowService service, SlideService slideService, SlideRepository slideRepository) {
            super(service, SlideShow.class, slideService, slideRepository, Slide.class);
            this.slideRepository = slideRepository;
        }

        @Override
        public List<PermissionValue> getPermissionValuesForAction(PermissionAction action, SlideShow slideShow) {
            PermissionValue permissionValue = null;
            switch (action) {
            case CREATE:
                permissionValue = PermissionType.slideShows().createSlides().forPersistable(slideShow);
                break;
            case READ:
                permissionValue = PermissionType.slideShows().readSlides().forPersistable(slideShow);
                break;
            case UPDATE:
                permissionValue = PermissionType.slideShows().updateSlides().forPersistable(slideShow);
                break;
            case DELETE:
                permissionValue = PermissionType.slideShows().deleteSlides().forPersistable(slideShow);
                break;
            default:
                break;
            }
            return permissionValue != null ? Collections.singletonList(permissionValue) : Collections.emptyList();
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
            child.setDisplayOrder(Optional.ofNullable(slideRepository.getHighestDisplayOrder(parent.getId())).map(i -> i + 1L).orElse(1L));
            parent.addSlide(child);
        }
    };

    private SlideCrud slideCrud;
    private SlideService slideService;

    public SlideShowService(SlideShowRepository repository, SlideService slideService, SlideRepository slideRepository) {
        super(SlideShow.class, PermissionType::slideShows, repository);
        slideCrud = new SlideCrud(this, slideService, slideRepository);
        this.slideService = slideService;
    }

    private SlideShowRepository repo() {
        return (SlideShowRepository) repository;
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

    public SlideShow findByIdAlias(String idAlias) {
        return repo().findOneByIdAlias(idAlias);
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
