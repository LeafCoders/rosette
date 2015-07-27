package se.leafcoders.rosette.controller;

import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import se.leafcoders.rosette.comparator.PosterComparator;
import se.leafcoders.rosette.model.Poster;
import se.leafcoders.rosette.service.PosterService;

@Controller
public class PosterController extends AbstractController {
	@Autowired
	private PosterService posterService;

	@RequestMapping(value = "posters/{id}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Poster getPoster(@PathVariable String id) {
		return posterService.read(id);
	}

	@RequestMapping(value = "posters", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<Poster> getPosters(HttpServletResponse response) {
		List<Poster> posters = posterService.readMany(new Query());
        Collections.sort(posters, new PosterComparator());
		return posters;
	}

	@RequestMapping(value = "posters", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Poster postPoster(@RequestBody Poster poster, HttpServletResponse response) {
		return posterService.create(poster, response);
	}

	@RequestMapping(value = "posters/{id}", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	public void putPoster(@PathVariable String id, HttpServletRequest request, HttpServletResponse response) {
		posterService.update(id, request, response);
	}

	@RequestMapping(value = "posters/{id}", method = RequestMethod.DELETE, produces = "application/json")
	public void deletePoster(@PathVariable String id, HttpServletResponse response) {
		posterService.delete(id, response);
	}
}
