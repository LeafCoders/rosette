package se.leafcoders.rosette.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import se.leafcoders.rosette.model.education.EducationTheme;
import se.leafcoders.rosette.service.EducationThemeService;
import se.leafcoders.rosette.util.ManyQuery;

@Controller
public class EducationThemeController extends AbstractController {
    @Autowired
    private EducationThemeService educationThemeService;

	@RequestMapping(value = "educationThemes/{id}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public EducationTheme getEducationTheme(@PathVariable String id) {
		return educationThemeService.read(id);
	}

	@RequestMapping(value = "educationThemes", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<EducationTheme> getEducationThemes(
	        @RequestParam(required = false) String educationTypeId,
	        HttpServletRequest request
    ) {
	    ManyQuery manyQuery = new ManyQuery(request, "-id");
	    if (educationTypeId != null) {
	        manyQuery.addCriteria(Criteria.where("educationType.id").is(educationTypeId));
	    }
		return educationThemeService.readMany(manyQuery);
	}

	@RequestMapping(value = "educationThemes", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public EducationTheme postEducationTheme(@RequestBody EducationTheme educationType, HttpServletResponse response) {
		return educationThemeService.create(educationType, response);
	}

    @RequestMapping(value = "educationThemes/{id}", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
    public void putEducationTheme(@PathVariable String id, HttpServletRequest request, HttpServletResponse response) {
		educationThemeService.update(id, request, response);
    }

	@RequestMapping(value = "educationThemes/{id}", method = RequestMethod.DELETE, produces = "application/json")
	public void deleteEducationTheme(@PathVariable String id, HttpServletResponse response) {
		educationThemeService.delete(id, response);
	}
}
