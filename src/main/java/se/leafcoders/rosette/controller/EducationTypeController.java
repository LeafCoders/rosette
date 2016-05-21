package se.leafcoders.rosette.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import se.leafcoders.rosette.model.education.EducationType;
import se.leafcoders.rosette.service.EducationTypeService;
import se.leafcoders.rosette.util.ManyQuery;

@RestController
public class EducationTypeController extends ApiV1Controller {
    @Autowired
    private EducationTypeService educationTypeService;

	@RequestMapping(value = "educationTypes/{id}", method = RequestMethod.GET, produces = "application/json")
	public EducationType getEducationType(@PathVariable String id) {
		return educationTypeService.read(id);
	}

	@RequestMapping(value = "educationTypes", method = RequestMethod.GET, produces = "application/json")
	public List<EducationType> getEducationTypes(HttpServletRequest request) {
		return educationTypeService.readMany(new ManyQuery(request, "title"));
	}

	// EducationType must contain the attribute 'type' that equals any string specified in EducationType  
	@RequestMapping(value = "educationTypes", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public EducationType postEducationType(@RequestBody EducationType educationType, HttpServletResponse response) {
		return educationTypeService.create(educationType, response);
	}

	// EducationType must contain the attribute 'type' that equals any string specified in EducationType  
    @RequestMapping(value = "educationTypes/{id}", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
    public void putEducationType(@PathVariable String id, HttpServletRequest request, HttpServletResponse response) {
		educationTypeService.update(id, request, response);
    }

	@RequestMapping(value = "educationTypes/{id}", method = RequestMethod.DELETE, produces = "application/json")
	public void deleteEducationType(@PathVariable String id, HttpServletResponse response) {
		educationTypeService.delete(id, response);
	}
}
