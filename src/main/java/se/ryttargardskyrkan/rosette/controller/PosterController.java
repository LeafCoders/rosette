package se.ryttargardskyrkan.rosette.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import se.ryttargardskyrkan.rosette.comparator.PosterComparator;
import se.ryttargardskyrkan.rosette.model.Poster;
import se.ryttargardskyrkan.rosette.service.PosterService;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;

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
		List<Poster> posters = posterService.readMany(null);
        Collections.sort(posters, new PosterComparator());
		return posters;
	}

	@RequestMapping(value = "posters", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Poster postPoster(@RequestBody Poster poster, HttpServletResponse response) {
		return posterService.create(poster, response);
	}

	@RequestMapping(value = "posters/{id}", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	public void putPoster(@PathVariable String id, @RequestBody Poster poster, HttpServletResponse response) {
		Update update = new Update();
		update.set("title", poster.getTitle());
		update.set("startTime", poster.getStartTime());
		update.set("endTime", poster.getEndTime());
		update.set("duration", poster.getDuration());
		update.set("image", poster.getImage());

		posterService.update(id, poster, update, response);
	}

	@RequestMapping(value = "posters/{id}", method = RequestMethod.DELETE, produces = "application/json")
	public void deletePoster(@PathVariable String id, HttpServletResponse response) {
		posterService.delete(id, response);
	}
}
