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
import se.leafcoders.rosette.controller.dto.ArticleSerieIn;
import se.leafcoders.rosette.controller.dto.ArticleSerieOut;
import se.leafcoders.rosette.persistence.service.ArticleSerieService;

@RestController
@RequestMapping(value = "api/articleSeries", produces = "application/json")
public class ArticleSeriesController {

    @Autowired
    private ArticleSerieService articleSerieService;

    @GetMapping(value = "/{id}")
    public ArticleSerieOut getArticleSerie(@PathVariable Long id) {
        return articleSerieService.toOut(articleSerieService.read(id, true));
    }

    @GetMapping
    public Collection<ArticleSerieOut> getArticleSeries(HttpServletRequest request, @RequestParam Long articleTypeId) {
        return articleSerieService.toOut(articleSerieService.findAllOfType(articleTypeId, true));
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<ArticleSerieOut> postArticleSerie(@RequestBody ArticleSerieIn articleSerie) {
        return new ResponseEntity<ArticleSerieOut>(articleSerieService.toOut(articleSerieService.create(articleSerie, true)), HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", consumes = "application/json")
    public ArticleSerieOut putArticleSerie(@PathVariable Long id, HttpServletRequest request) {
        return articleSerieService.toOut(articleSerieService.update(id, ArticleSerieIn.class, request, true));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteArticleSerie(@PathVariable Long id) {
        return articleSerieService.delete(id, true);
    }

}
