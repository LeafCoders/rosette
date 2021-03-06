package se.leafcoders.rosette.endpoint.slideshow;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import se.leafcoders.rosette.core.permission.PermissionAction;
import se.leafcoders.rosette.core.permission.PermissionValue;
import se.leafcoders.rosette.core.persistable.ChildCrud;
import se.leafcoders.rosette.core.persistable.PersistenceService;
import se.leafcoders.rosette.endpoint.assetfolder.AssetFolderService;

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
                permissionValue = new SlideShowPermissionValue().createSlides().forPersistable(slideShow);
                break;
            case READ:
                permissionValue = new SlideShowPermissionValue().readSlides().forPersistable(slideShow);
                break;
            case UPDATE:
                permissionValue = new SlideShowPermissionValue().updateSlides().forPersistable(slideShow);
                break;
            case DELETE:
                permissionValue = new SlideShowPermissionValue().deleteSlides().forPersistable(slideShow);
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
        super(SlideShow.class, SlideShowPermissionValue::new, repository);
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
