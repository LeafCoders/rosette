package se.leafcoders.rosette.endpoint.podcast;

import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.data.domain.Sort;
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
import se.leafcoders.rosette.endpoint.article.Article;
import se.leafcoders.rosette.endpoint.article.ArticleService;
import se.leafcoders.rosette.util.PodcastFeedGenerator;

@RequiredArgsConstructor
@Transactional
@RestController
@RequestMapping(value = "api/podcasts", produces = "application/json")
public class PodcastController {

    private final PodcastService podcastService;
    private final ArticleService articleService;
    private final PodcastFeedGenerator podcastFeedGenerator;

    @GetMapping(value = "/{id}")
    public PodcastOut getPodcast(@PathVariable Long id) {
        return podcastService.toOut(podcastService.read(id, true));
    }

    @GetMapping
    public Collection<PodcastOut> getPodcasts(HttpServletRequest request) {
        Sort sort = Sort.by("title").ascending();
        return podcastService.toOut(podcastService.readMany(sort, true));
    }

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public PodcastOut postPodcast(@RequestBody PodcastIn podcast) {
        return podcastService.toOut(podcastService.create(podcast, true));
    }

    @PutMapping(value = "/{id}", consumes = "application/json")
    public PodcastOut putPodcast(@PathVariable Long id, HttpServletRequest request) {
        return podcastService.toOut(podcastService.update(id, PodcastIn.class, request, true));
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePodcast(@PathVariable Long id) {
        podcastService.delete(id, true);
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
