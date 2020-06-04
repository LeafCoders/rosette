package se.leafcoders.rosette.endpoint.articletype;

import java.util.Collection;

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

@RequiredArgsConstructor
@Transactional
@RestController
@RequestMapping(value = "api/articleTypes", produces = "application/json")
public class ArticleTypeController {

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
    @ResponseStatus(HttpStatus.CREATED)
    public ArticleTypeOut postArticleType(@RequestBody ArticleTypeIn articleType) {
        return articleTypeService.toOut(articleTypeService.create(articleType, true));
    }

    @PutMapping(value = "/{id}", consumes = "application/json")
    public ArticleTypeOut putArticleType(@PathVariable Long id, HttpServletRequest request) {
        return articleTypeService.toOut(articleTypeService.update(id, ArticleTypeIn.class, request, true));
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteArticleType(@PathVariable Long id) {
        articleTypeService.delete(id, true);
    }

}
