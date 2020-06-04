package se.leafcoders.rosette.endpoint.slideshow;

import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import se.leafcoders.rosette.core.exception.NotFoundException;

@RequiredArgsConstructor
@Transactional
@RestController
@RequestMapping(value = "api/slideShows", produces = "application/json")
public class SlideShowController {

    private final SlideService slideService;
    private final SlideShowService slideShowService;

    @GetMapping(value = "/{id}")
    public SlideShowOut getSlideShow(@PathVariable Long id) {
        return slideShowService.toOut(slideShowService.read(id, true));
    }

    @GetMapping
    public Collection<SlideShowOut> getSlideShows(HttpServletRequest request) {
        return slideShowService.toOut(slideShowService.readMany(true));
    }

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public SlideShowOut postSlideShow(@RequestBody SlideShowIn slideShow) {
        return slideShowService.toOut(slideShowService.create(slideShow, true));
    }

    @PutMapping(value = "/{id}", consumes = "application/json")
    public SlideShowOut putSlideShow(@PathVariable Long id, HttpServletRequest request) {
        return slideShowService.toOut(slideShowService.update(id, SlideShowIn.class, request, true));
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSlideShow(@PathVariable Long id) {
        slideShowService.delete(id, true);
    }

    // Slides

    @GetMapping(value = "/{id}/slides")
    public Collection<SlideOut> getSlidesInSlideShow(@PathVariable Long id) {
        return slideService.toOut(slideShowService.readSlides(id));
    }

    @PostMapping(value = "/{id}/slides", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public SlideOut addSlideToSlideShow(@PathVariable Long id, @RequestBody SlideIn slide) {
        return slideService.toOut(slideShowService.addSlide(id, slide));
    }

    @PutMapping(value = "/{id}/slides/{slideId}", consumes = "application/json")
    public SlideOut updateSlideInSlideShow(@PathVariable Long id, @PathVariable Long slideId,
            HttpServletRequest request) {
        return slideService.toOut(slideShowService.updateSlide(id, slideId, request));
    }

    @DeleteMapping(value = "/{id}/slides/{slideId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSlideInSlideShow(@PathVariable Long id, @PathVariable Long slideId) {
        slideShowService.deleteSlide(id, slideId);
    }

    @PutMapping(value = "/{id}/slides/{slideId}/moveTo/{toSlideId}", consumes = "application/json")
    public Collection<SlideOut> moveSlideInSlideShow(@PathVariable Long id, @PathVariable Long slideId,
            @PathVariable Long toSlideId) {
        slideService.moveSlide(id, slideId, toSlideId);
        return slideService.toOut(slideShowService.readSlides(id));
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
