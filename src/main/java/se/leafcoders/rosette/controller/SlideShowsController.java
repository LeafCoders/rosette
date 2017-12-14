package se.leafcoders.rosette.controller;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import se.leafcoders.rosette.controller.dto.SlideIn;
import se.leafcoders.rosette.controller.dto.SlideOut;
import se.leafcoders.rosette.controller.dto.SlideShowIn;
import se.leafcoders.rosette.controller.dto.SlideShowOut;
import se.leafcoders.rosette.persistence.service.SlideService;
import se.leafcoders.rosette.persistence.service.SlideShowService;

@RestController
@RequestMapping(value = "api/slideShows", produces = "application/json")
public class SlideShowsController {

    @Autowired
    private SlideShowService slideShowService;

    @Autowired
    private SlideService slideService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public SlideShowOut getSlideShow(@PathVariable Long id) {
        return slideShowService.toOut(slideShowService.read(id, true));
    }

    @RequestMapping(method = RequestMethod.GET)
    public Collection<SlideShowOut> getSlideShows(HttpServletRequest request) {
        return slideShowService.toOut(slideShowService.readMany(true));
    }

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<SlideShowOut> postSlideShow(@RequestBody SlideShowIn slideShow) {
        return new ResponseEntity<SlideShowOut>(slideShowService.toOut(slideShowService.create(slideShow, true)), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = "application/json")
    public SlideShowOut putSlideShow(@PathVariable Long id, HttpServletRequest request) {
        return slideShowService.toOut(slideShowService.update(id, SlideShowIn.class, request, true));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteSlideShow(@PathVariable Long id) {
        return slideShowService.delete(id, true);
    }

    // Slides
    
    @RequestMapping(value = "/{id}/slides", method = RequestMethod.GET)
    public Collection<SlideOut> getSlidesInSlideShow(@PathVariable Long id) {
        return slideService.toOut(slideShowService.readSlides(id));
    }

    @RequestMapping(value = "/{id}/slides", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<SlideOut> addSlideToSlideShow(@PathVariable Long id, @RequestBody SlideIn slide) {
        return new ResponseEntity<SlideOut>(slideService.toOut(slideShowService.addSlide(id, slide)), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}/slides/{slideId}", method = RequestMethod.PUT, consumes = "application/json")
    public SlideOut updateSlideInSlideShow(@PathVariable Long id, @PathVariable Long slideId, HttpServletRequest request) {
        return slideService.toOut(slideShowService.updateSlide(id, slideId, request));
    }

    @RequestMapping(value = "/{id}/slides/{slideId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteSlideInSlideShow(@PathVariable Long id, @PathVariable Long slideId, HttpServletRequest request) {
        return slideShowService.deleteSlide(id, slideId);
    }
}
