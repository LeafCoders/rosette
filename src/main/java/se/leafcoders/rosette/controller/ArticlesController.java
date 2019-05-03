package se.leafcoders.rosette.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import se.leafcoders.rosette.controller.dto.ArticleIn;
import se.leafcoders.rosette.controller.dto.ArticleOut;
import se.leafcoders.rosette.controller.dto.ArticlePublicOut;
import se.leafcoders.rosette.controller.dto.ArticlesPublicOut;
import se.leafcoders.rosette.controller.dto.ResourceOut;
import se.leafcoders.rosette.persistence.model.Article;
import se.leafcoders.rosette.persistence.service.ArticleService;
import se.leafcoders.rosette.persistence.service.AssetService;
import se.leafcoders.rosette.persistence.service.ResourceService;

@Transactional
@RestController
@RequestMapping(value = "api/articles", produces = "application/json")
public class ArticlesController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private ResourceService resourceService;
    
    @Autowired
    private AssetService assetService;
    
    @GetMapping(value = "/{id}")
    public ArticleOut getArticle(@PathVariable Long id) {
        return articleService.toOut(articleService.read(id, true));
    }

    @GetMapping
    public Collection<ArticleOut> getArticles(
            @RequestParam Long articleTypeId,
            @RequestParam(value = "from", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime from,            
            @RequestParam(value = "before", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime before
    ) {
        Sort sort = new Sort(Sort.Direction.ASC, "time");

        Specification<Article> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("articleTypeId"), articleTypeId));
            Optional.ofNullable(from).ifPresent(time -> predicates.add(cb.greaterThanOrEqualTo(root.get("time"), time)));
            Optional.ofNullable(before).ifPresent(time -> predicates.add(cb.lessThan(root.get("time"), time)));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        return articleService.toOut(articleService.readMany(spec, sort, true));
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<ArticleOut> postArticle(@RequestBody ArticleIn article) {
        return new ResponseEntity<ArticleOut>(articleService.toOut(articleService.create(article, true)), HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", consumes = "application/json")
    public ArticleOut putArticle(@PathVariable Long id, HttpServletRequest request) {
        return articleService.toOut(articleService.update(id, ArticleIn.class, request, true));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {
        return articleService.delete(id, true);
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

    @GetMapping(value = "/public/{id}")
    public ArticlePublicOut getPublicArticle(@PathVariable Long id) {
        articleService.checkPublicPermission();
        return new ArticlePublicOut(assetService, articleService.read(id, false));
    }
    
    @GetMapping(value = "/public/chronologic")
    public ArticlesPublicOut getPublicArticlesInChronologicOrder(
            @RequestParam(value = "page", required = false) Integer page,            
            @RequestParam(value = "pageSize", required = false, defaultValue="20") int pageSize,
            @RequestParam(value = "articleTypeId", required = false) Long articleTypeId,
            @RequestParam(value = "articleSerieId", required = false) Long articleSerieId
        ) {
        articleService.checkPublicPermission();
        
        Sort sort = new Sort(Sort.Direction.ASC, "time");

        Specification<Article> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            Optional.ofNullable(articleTypeId).ifPresent(id -> predicates.add(cb.equal(root.get("articleTypeId"), id)));
            Optional.ofNullable(articleSerieId).ifPresent(id -> predicates.add(cb.equal(root.get("articleSerieId"), id)));
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        List<Article> publicArticles = articleService.readMany(spec, PageRequest.of(page, pageSize, sort), false);
        return new ArticlesPublicOut(assetService, publicArticles);
    }

    @GetMapping(value = "/public/fromnow")
    public ArticlesPublicOut getPublicArticlesStartingFromNow(
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,            
            @RequestParam(value = "pageSize", required = false, defaultValue="20") int pageSize,
            @RequestParam(value = "future", required = false, defaultValue = "false") boolean future,            
            @RequestParam(value = "articleTypeId", required = false) Long articleTypeId,
            @RequestParam(value = "articleSerieId", required = false) Long articleSerieId
        ) {
        articleService.checkPublicPermission();
        
        Sort sort = new Sort(future ? Sort.Direction.ASC : Sort.Direction.DESC, "time");
        
        Specification<Article> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (future) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("time"), LocalDateTime.now()));
            } else {
                predicates.add(cb.lessThan(root.get("time"), LocalDateTime.now()));
            }
            Optional.ofNullable(articleTypeId).ifPresent(id -> predicates.add(cb.equal(root.get("articleTypeId"), id)));
            Optional.ofNullable(articleSerieId).ifPresent(id -> predicates.add(cb.equal(root.get("articleSerieId"), id)));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        List<Article> publicArticles;
        if (pageSize > 0) {
            publicArticles = articleService.readMany(spec, PageRequest.of(page, pageSize, sort), false);
        } else {
            publicArticles = articleService.readMany(spec, sort, false);
        }
        return new ArticlesPublicOut(assetService, publicArticles);
    }

}
