package se.leafcoders.rosette.controller;

import java.util.Collection;
import javax.servlet.http.HttpServletRequest;
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
import se.leafcoders.rosette.controller.dto.ArticleSerieIn;
import se.leafcoders.rosette.controller.dto.ArticleSerieOut;
import se.leafcoders.rosette.persistence.service.ArticleSerieService;

@RestController
@RequestMapping(value = "api/postSeries", produces = "application/json")
public class PostSeriesController {

    @Autowired
    private ArticleSerieService postSerieService;

    @GetMapping(value = "/{id}")
    public ArticleSerieOut getPostSerie(@PathVariable Long id) {
        return postSerieService.toOut(postSerieService.read(id, true));
    }

    @GetMapping()
    public Collection<ArticleSerieOut> getPostSeries(HttpServletRequest request) {
        Sort sort = new Sort(Sort.Direction.ASC, "name");        
        return postSerieService.toOut(postSerieService.readMany(sort, true));
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<ArticleSerieOut> postPostSerie(@RequestBody ArticleSerieIn postSerie) {
        return new ResponseEntity<ArticleSerieOut>(postSerieService.toOut(postSerieService.create(postSerie, true)), HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", consumes = "application/json")
    public ArticleSerieOut putPostSerie(@PathVariable Long id, HttpServletRequest request) {
        return postSerieService.toOut(postSerieService.update(id, ArticleSerieIn.class, request, true));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deletePostSerie(@PathVariable Long id) {
        return postSerieService.delete(id, true);
    }

}
