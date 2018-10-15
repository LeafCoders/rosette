package se.leafcoders.rosette.controller;

import java.util.Collection;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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
import se.leafcoders.rosette.controller.dto.PodcastIn;
import se.leafcoders.rosette.controller.dto.PodcastOut;
import se.leafcoders.rosette.exception.NotFoundException;
import se.leafcoders.rosette.persistence.model.Article;
import se.leafcoders.rosette.persistence.model.Podcast;
import se.leafcoders.rosette.persistence.service.ArticleService;
import se.leafcoders.rosette.persistence.service.PodcastService;
import se.leafcoders.rosette.util.PodcastFeedGenerator;

@Transactional
@RestController
@RequestMapping(value = "api/podcasts", produces = "application/json")
public class PodcastsController {

    @Autowired
    private PodcastService podcastService;

    @Autowired
    private ArticleService articleService;
    
    @Autowired
    private PodcastFeedGenerator podcastFeedGenerator;

    @GetMapping(value = "/{id}")
    public PodcastOut getPodcast(@PathVariable Long id) {
        return podcastService.toOut(podcastService.read(id, true));
    }

    @GetMapping
    public Collection<PodcastOut> getPodcasts(HttpServletRequest request) {
        Sort sort = new Sort(Sort.Direction.ASC, "title");        
        return podcastService.toOut(podcastService.readMany(sort, true));
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<PodcastOut> postPodcast(@RequestBody PodcastIn podcast) {
        return new ResponseEntity<PodcastOut>(podcastService.toOut(podcastService.create(podcast, true)), HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", consumes = "application/json")
    public PodcastOut putPodcast(@PathVariable Long id, HttpServletRequest request) {
        return podcastService.toOut(podcastService.update(id, PodcastIn.class, request, true));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deletePodcast(@PathVariable Long id) {
        return podcastService.delete(id, true);
    }
    
    // Public

    @GetMapping(value = "/feed/{idAlias}", produces = "application/rss+xml; charset=UTF-8")
    public String getPodcastFeed(@PathVariable String idAlias) {
        Podcast podcast = podcastService.findByIdAlias(idAlias);
        if (podcast == null) {
            throw new NotFoundException(Podcast.class, idAlias);
        }

        podcastService.checkPublicPermission(podcast.getId());

        List<Article> articles = articleService.findAllOfType(podcast.getArticleTypeId(), false);
        return podcastFeedGenerator.getPodcastFeed(podcast, articles);
    }

    
}
