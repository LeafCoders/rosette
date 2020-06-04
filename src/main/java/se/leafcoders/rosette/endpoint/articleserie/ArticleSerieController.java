package se.leafcoders.rosette.endpoint.articleserie;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@RestController
@RequestMapping(value = "api/articleSeries", produces = "application/json")
public class ArticleSerieController {

    private final ArticleSerieService articleSerieService;

    @GetMapping(value = "/{id}")
    public ArticleSerieOut getArticleSerie(@PathVariable Long id) {
        return articleSerieService.toOut(articleSerieService.read(id, true));
    }

    @GetMapping
    public Collection<ArticleSerieOut> getArticleSeries(HttpServletRequest request, @RequestParam Long articleTypeId) {
        return articleSerieService.toOut(articleSerieService.findAllOfType(articleTypeId, true));
    }

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ArticleSerieOut postArticleSerie(@RequestBody ArticleSerieIn articleSerie) {
        return articleSerieService.toOut(articleSerieService.create(articleSerie, true));
    }

    @PutMapping(value = "/{id}", consumes = "application/json")
    public ArticleSerieOut putArticleSerie(@PathVariable Long id, HttpServletRequest request) {
        return articleSerieService.toOut(articleSerieService.update(id, ArticleSerieIn.class, request, true));
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteArticleSerie(@PathVariable Long id) {
        articleSerieService.delete(id, true);
    }

}
