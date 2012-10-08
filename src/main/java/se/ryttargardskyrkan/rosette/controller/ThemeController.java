package se.ryttargardskyrkan.rosette.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import se.ryttargardskyrkan.rosette.exception.NotFoundException;
import se.ryttargardskyrkan.rosette.model.Event;
import se.ryttargardskyrkan.rosette.model.Theme;

@Controller
public class ThemeController extends AbstractController {
	private final MongoTemplate mongoTemplate;

	@Autowired
	public ThemeController(MongoTemplate mongoTemplate) {
		super();
		this.mongoTemplate = mongoTemplate;
	}

	@RequestMapping(value = "themes/{id}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Theme getTheme(@PathVariable String id) {
		Theme theme = mongoTemplate.findById(id, Theme.class);
		if (theme == null) {
			throw new NotFoundException();
		}
		return theme;
	}

	@RequestMapping(value = "themes", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<Theme> getThemes(
			@RequestParam(required = false) Integer page,
			@RequestParam(required = false) Integer per_page,
			HttpServletResponse response) {
		Query query = new Query();
		query.sort().on("title", Order.ASCENDING);
		
		int thePage = 1;
		int thePerPage = 20;
		
		if (page != null && page > 0) {
			thePage = page;
		}
		if (per_page != null && per_page > 0) {
			thePerPage = per_page;
		}		
		
		query.limit(thePerPage);
		query.skip((thePage - 1) * thePerPage);
				
		List<Theme> themes = mongoTemplate.find(query, Theme.class);

		// Header links
		Query queryForLinks = new Query();
		long numberOfThemes = mongoTemplate.count(queryForLinks, Theme.class);

		if (numberOfThemes > 0) {
			StringBuilder sb = new StringBuilder();
			String delimiter = "";

			if (thePage - 1 > 0) {
				sb.append(delimiter);
				sb.append("<themes?page=" + (thePage - 1));
				if (per_page != null) {
					sb.append("&per_page=" + per_page);
				}

				sb.append(">; rel=\"previous\"");
				delimiter = ",";
			}

			if (numberOfThemes > thePage * thePerPage) {
				sb.append(delimiter);
				sb.append("<themes?page=" + (thePage + 1));
				if (per_page != null) {
					sb.append("&per_page=" + per_page);
				}
				sb.append(">; rel=\"next\"");
				delimiter = ",";
			}

			response.setHeader("Link", sb.toString());
		}

		return themes;
	}

	@RequestMapping(value = "themes", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Theme postTheme(@RequestBody Theme theme, HttpServletResponse response) {
//		checkPermission("themes:create");
		validate(theme);

		mongoTemplate.insert(theme);

		response.setStatus(HttpStatus.CREATED.value());
		return theme;
	}

	@RequestMapping(value = "themes/{id}", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	public void putTheme(@PathVariable String id, @RequestBody Theme theme, HttpServletResponse response) {
//		checkPermission("themes:update");
		validate(theme);

		Update update = new Update();
		if (theme.getTitle() != null)
			update.set("title", theme.getTitle());
		if (theme.getDescription() != null)
			update.set("description", theme.getDescription());

		if (mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(id)), update, Theme.class).getN() == 0) {
			throw new NotFoundException();
		}

		response.setStatus(HttpStatus.OK.value());
	}

	@RequestMapping(value = "themes/{id}", method = RequestMethod.DELETE, produces = "application/json")
	public void deleteTheme(@PathVariable String id, HttpServletResponse response) {
//		checkPermission("themes:delete:" + id);

		Theme deletedTheme = mongoTemplate.findAndRemove(Query.query(Criteria.where("id").is(id)), Theme.class);
		if (deletedTheme == null) {
			throw new NotFoundException();
		} else {
			response.setStatus(HttpStatus.OK.value());
		}
	}
}
