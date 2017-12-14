package se.leafcoders.rosette.controller;

import java.util.Collection;
import javax.servlet.http.HttpServletRequest;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import se.leafcoders.rosette.controller.dto.ArticleIn;
import se.leafcoders.rosette.controller.dto.ArticleOut;
import se.leafcoders.rosette.persistence.service.ArticleService;

@RestController
@RequestMapping(value = "api/articles", produces = "application/json")
public class ArticlesController {

    @Autowired
    private ArticleService articleService;

    @GetMapping(value = "/{id}")
    public ArticleOut getArticle(@PathVariable Long id) {
        return articleService.toOut(articleService.read(id, true));
    }

    @GetMapping
    public Collection<ArticleOut> getArticles(HttpServletRequest request, @RequestParam Long articleTypeId) {
        return articleService.toOut(articleService.findAllOfType(articleTypeId, true));
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

}
