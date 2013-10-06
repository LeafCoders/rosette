package se.ryttargardskyrkan.rosette.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import se.ryttargardskyrkan.rosette.comparator.PosterComparator;
import se.ryttargardskyrkan.rosette.exception.NotFoundException;
import se.ryttargardskyrkan.rosette.model.Poster;
import se.ryttargardskyrkan.rosette.security.MongoRealm;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
public class PosterController extends AbstractController {
	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private MongoRealm mongoRealm;

	@RequestMapping(value = "posters/{id}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Poster getPoster(@PathVariable String id) {
		checkPermission("read:posters:" + id);

        Poster poster = mongoTemplate.findById(id, Poster.class);
		if (poster == null) {
			throw new NotFoundException();
		}
		return poster;
	}

	@RequestMapping(value = "posters", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<Poster> getPosters(HttpServletResponse response) {
		List<Poster> postersInDatabase = mongoTemplate.find(new Query(), Poster.class);
		List<Poster> posters = new ArrayList<Poster>();
		if (postersInDatabase != null) {
			for (Poster posterInDatabase : postersInDatabase) {
				if (isPermitted("read:posters:" + posterInDatabase.getId())) {
					posters.add(posterInDatabase);
				}
			}
		}
        Collections.sort(posters, new PosterComparator());
		return posters;
	}

	@RequestMapping(value = "posters", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Poster postPoster(@RequestBody Poster poster, HttpServletResponse response) {
		checkPermission("create:posters");
		validate(poster);
		
		mongoTemplate.insert(poster);
		
		response.setStatus(HttpStatus.CREATED.value());
		return poster;
	}

	@RequestMapping(value = "posters/{id}", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	public void putPoster(@PathVariable String id, @RequestBody Poster poster, HttpServletResponse response) {
		checkPermission("update:posters:" + id);
		validate(poster);

		Update update = new Update();
		update.set("title", poster.getTitle());
		update.set("startTime", poster.getStartTime());
		update.set("endTime", poster.getEndTime());
		update.set("duration", poster.getDuration());

		if (mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(id)), update, Poster.class).getN() == 0) {
			throw new NotFoundException();
		}

		response.setStatus(HttpStatus.OK.value());
	}

	@RequestMapping(value = "posters/{id}", method = RequestMethod.DELETE, produces = "application/json")
	public void deleteGroup(@PathVariable String id, HttpServletResponse response) {
		checkPermission("delete:posters:" + id);

        Poster deletedPoster = mongoTemplate.findAndRemove(Query.query(Criteria.where("id").is(id)), Poster.class);
		if (deletedPoster == null) {
			throw new NotFoundException();
		} else {
			response.setStatus(HttpStatus.OK.value());
		}
	}
}
