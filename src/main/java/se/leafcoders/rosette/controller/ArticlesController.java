package se.leafcoders.rosette.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import se.leafcoders.rosette.controller.dto.ArticleIn;
import se.leafcoders.rosette.controller.dto.ArticleOut;
import se.leafcoders.rosette.controller.dto.ArticlePublicOut;
import se.leafcoders.rosette.controller.dto.ArticlesPublicOut;
import se.leafcoders.rosette.controller.dto.ResourceOut;
import se.leafcoders.rosette.persistence.converter.ClientServerTime;
import se.leafcoders.rosette.persistence.model.Article;
import se.leafcoders.rosette.persistence.service.ArticleService;
import se.leafcoders.rosette.persistence.service.AssetService;
import se.leafcoders.rosette.persistence.service.ResourceService;
import se.leafcoders.rosette.util.IdToSlugConverter;

@RequiredArgsConstructor
@Transactional
@RestController
@RequestMapping(value = "api/articles", produces = "application/json")
public class ArticlesController {

    private final ArticleService articleService;
    private final ResourceService resourceService;
    private final AssetService assetService;

    @GetMapping(value = "/{id}")
    public ArticleOut getArticle(@PathVariable Long id) {
        return articleService.toOut(articleService.read(id, true));
    }

    @GetMapping
    public Collection<ArticleOut> getArticles(@RequestParam Long articleTypeId,
            @RequestParam(value = "from", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime from,
            @RequestParam(value = "before", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime before) {
        final Optional<LocalDateTime> fromOptional = Optional.ofNullable(ClientServerTime.clientToServer(from));
        final Optional<LocalDateTime> beforeOptional = Optional.ofNullable(ClientServerTime.clientToServer(before));
        Sort sort = Sort.by("time").ascending();

        Specification<Article> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("articleTypeId"), articleTypeId));
            fromOptional.ifPresent(time -> predicates.add(cb.greaterThanOrEqualTo(root.get("time"), time)));
            beforeOptional.ifPresent(time -> predicates.add(cb.lessThan(root.get("time"), time)));
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return articleService.toOut(articleService.readMany(spec, sort, true));
    }

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ArticleOut postArticle(@RequestBody ArticleIn article) {
        return articleService.toOut(articleService.create(article, true));
    }

    @PutMapping(value = "/{id}", consumes = "application/json")
    public ArticleOut putArticle(@PathVariable Long id, HttpServletRequest request) {
        return articleService.toOut(articleService.update(id, ArticleIn.class, request, true));
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteArticle(@PathVariable Long id) {
        articleService.delete(id, true);
    }

    @GetMapping(value = "/{id}/authors")
    public Collection<ResourceOut> getAuthors(@PathVariable Long id) {
        return resourceService.toOut(articleService.getAuthors(id));
    }

    @PostMapping(value = "/{id}/authors/{authorId}", consumes = "application/json")
    public Collection<ResourceOut> addAuthor(@PathVariable Long id, @PathVariable Long authorId) {
        return resourceService.toOut(articleService.addAuthor(id, authorId));
    }

    @DeleteMapping(value = "/{id}/authors/{authorId}")
    public Collection<ResourceOut> removeAuthor(@PathVariable Long id, @PathVariable Long authorId) {
        return resourceService.toOut(articleService.removeAuthor(id, authorId));
    }

    // Public

    @GetMapping(value = "/public/{slug}")
    public ArticlePublicOut getPublicArticle(@PathVariable String slug) {
        articleService.checkPublicPermission();
        final Long id = IdToSlugConverter.convertSlugToId(slug);
        final Article article = articleService.read(id, false);
        if (article == null) {
            throw articleService.notFoundException(id);
        }
        return new ArticlePublicOut(assetService, article);
    }

    @GetMapping(value = "/public/articleType/{articleTypeId}")
    public ArticlesPublicOut getPublicArticles(@PathVariable Long articleTypeId,
            @RequestParam(value = "articleSerieId", required = false) Long articleSerieId,
            @RequestParam(value = "chronologic", required = false, defaultValue = "true") boolean chronologic,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "20") int pageSize,
            @RequestParam(value = "from", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime from,
            @RequestParam(value = "before", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime before) {
        articleService.checkPublicPermission();

        final Optional<LocalDateTime> fromOptional = Optional.ofNullable(ClientServerTime.clientToServer(from));
        final Optional<LocalDateTime> beforeOptional = Optional.ofNullable(ClientServerTime.clientToServer(before));
        Sort sort = Sort.by(chronologic ? Sort.Direction.ASC : Sort.Direction.DESC, "time");

        Specification<Article> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("articleTypeId"), articleTypeId));
            Optional.ofNullable(articleSerieId)
                    .ifPresent(id -> predicates.add(cb.equal(root.get("articleSerieId"), id)));
            fromOptional.ifPresent(time -> predicates.add(cb.greaterThanOrEqualTo(root.get("time"), time)));
            beforeOptional.ifPresent(time -> predicates.add(cb.lessThan(root.get("time"), time)));
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        List<Article> publicArticles = articleService.readMany(spec, PageRequest.of(page, pageSize, sort), false);
        return new ArticlesPublicOut(assetService, publicArticles);
    }

}
