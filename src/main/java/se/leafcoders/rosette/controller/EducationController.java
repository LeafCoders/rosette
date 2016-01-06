package se.leafcoders.rosette.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import se.leafcoders.rosette.model.education.Education;
import se.leafcoders.rosette.service.EducationService;
import se.leafcoders.rosette.util.ManyQuery;

@Controller
public class EducationController extends AbstractController {
    @Autowired
    private EducationService educationService;

	@RequestMapping(value = "educations/{id}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Education getEducation(@PathVariable String id) {
		return educationService.read(id);
	}

	@RequestMapping(value = "educations", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<Education> getEducations(HttpServletRequest request) {
		return educationService.readMany(new ManyQuery(request, "title"));
	}

	// Education must contain the attribute 'type' that equals any string specified in Education  
	@RequestMapping(value = "educations", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Education postEducation(@RequestBody Education education, HttpServletResponse response) {
		return educationService.create(education, response);
	}

	// Education must contain the attribute 'type' that equals any string specified in Education  
    @RequestMapping(value = "educations/{id}", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
    public void putEducation(@PathVariable String id, HttpServletRequest request, HttpServletResponse response) {
		educationService.update(id, request, response);
    }

	@RequestMapping(value = "educations/{id}", method = RequestMethod.DELETE, produces = "application/json")
	public void deleteEducation(@PathVariable String id, HttpServletResponse response) {
		educationService.delete(id, response);
	}
}
