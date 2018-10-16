package se.leafcoders.rosette.controller;

import java.util.Collection;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.leafcoders.rosette.controller.dto.SlideIn;
import se.leafcoders.rosette.controller.dto.SlideOut;
import se.leafcoders.rosette.controller.dto.SlideShowIn;
import se.leafcoders.rosette.controller.dto.SlideShowOut;
import se.leafcoders.rosette.controller.dto.SlideShowPublicOut;
import se.leafcoders.rosette.exception.NotFoundException;
import se.leafcoders.rosette.persistence.model.Slide;
import se.leafcoders.rosette.persistence.model.SlideShow;
import se.leafcoders.rosette.persistence.service.SlideService;
import se.leafcoders.rosette.persistence.service.SlideShowService;

@Transactional
@RestController
@RequestMapping(value = "api/slideShows", produces = "application/json")
public class SlideShowsController {

    @Autowired
    private SlideService slideService;
    
    @Autowired
    private SlideShowService slideShowService;

    @GetMapping(value = "/{id}")
    public SlideShowOut getSlideShow(@PathVariable Long id) {
        return slideShowService.toOut(slideShowService.read(id, true));
    }

    @GetMapping
    public Collection<SlideShowOut> getSlideShows(HttpServletRequest request) {
        return slideShowService.toOut(slideShowService.readMany(true));
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<SlideShowOut> postSlideShow(@RequestBody SlideShowIn slideShow) {
        return new ResponseEntity<SlideShowOut>(slideShowService.toOut(slideShowService.create(slideShow, true)), HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", consumes = "application/json")
    public SlideShowOut putSlideShow(@PathVariable Long id, HttpServletRequest request) {
        return slideShowService.toOut(slideShowService.update(id, SlideShowIn.class, request, true));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteSlideShow(@PathVariable Long id) {
        return slideShowService.delete(id, true);
    }

    // Slides
    
    @GetMapping(value = "/{id}/slides")
    public Collection<SlideOut> getSlidesInSlideShow(@PathVariable Long id) {
        return slideService.toOut(slideShowService.readSlides(id));
    }

    @PostMapping(value = "/{id}/slides", consumes = "application/json")
    public ResponseEntity<SlideOut> addSlideToSlideShow(@PathVariable Long id, @RequestBody SlideIn slide) {
        return new ResponseEntity<SlideOut>(slideService.toOut(slideShowService.addSlide(id, slide)), HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}/slides/{slideId}", consumes = "application/json")
    public SlideOut updateSlideInSlideShow(@PathVariable Long id, @PathVariable Long slideId, HttpServletRequest request) {
        return slideService.toOut(slideShowService.updateSlide(id, slideId, request));
    }

    @DeleteMapping(value = "/{id}/slides/{slideId}")
    public ResponseEntity<Void> deleteSlideInSlideShow(@PathVariable Long id, @PathVariable Long slideId, HttpServletRequest request) {
        return slideShowService.deleteSlide(id, slideId);
    }

    // Public

    @GetMapping(value = "/public/{idAlias}")
    public SlideShowPublicOut getPublicSlideShow(@PathVariable String idAlias) {
        SlideShow slideShow = slideShowService.findByIdAlias(idAlias);
        if (slideShow == null) {
            throw new NotFoundException(SlideShow.class, idAlias);
        }
        
        slideShowService.checkPublicPermission(slideShow.getId());
        
        List<Slide> slides = slideShow.getSlides();
        return new SlideShowPublicOut(slideShow, slideService.toOut(slides));
    }

}
