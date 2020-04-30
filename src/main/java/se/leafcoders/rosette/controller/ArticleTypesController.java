package se.leafcoders.rosette.controller;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

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

import lombok.RequiredArgsConstructor;
import se.leafcoders.rosette.controller.dto.ArticleTypeIn;
import se.leafcoders.rosette.controller.dto.ArticleTypeOut;
import se.leafcoders.rosette.persistence.service.ArticleTypeService;

@RequiredArgsConstructor
@Transactional
@RestController
@RequestMapping(value = "api/articleTypes", produces = "application/json")
public class ArticleTypesController {

    private final ArticleTypeService articleTypeService;

    @GetMapping(value = "/{id}")
    public ArticleTypeOut getArticleType(@PathVariable Long id) {
        return articleTypeService.toOut(articleTypeService.read(id, true));
    }

    @GetMapping
    public Collection<ArticleTypeOut> getArticleTypes(HttpServletRequest request) {
        return articleTypeService.toOut(articleTypeService.readMany(true));
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<ArticleTypeOut> postArticleType(@RequestBody ArticleTypeIn articleType) {
        return new ResponseEntity<ArticleTypeOut>(
                articleTypeService.toOut(articleTypeService.create(articleType, true)), HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", consumes = "application/json")
    public ArticleTypeOut putArticleType(@PathVariable Long id, HttpServletRequest request) {
        return articleTypeService.toOut(articleTypeService.update(id, ArticleTypeIn.class, request, true));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteArticleType(@PathVariable Long id) {
        return articleTypeService.delete(id, true);
    }

}
