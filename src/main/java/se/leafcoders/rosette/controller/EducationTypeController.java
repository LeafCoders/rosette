package se.leafcoders.rosette.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import se.leafcoders.rosette.model.education.EducationType;
import se.leafcoders.rosette.service.EducationTypeService;

@Controller
public class EducationTypeController extends AbstractController {
    @Autowired
    private EducationTypeService educationTypeService;

	@RequestMapping(value = "educationTypes/{id}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public EducationType getEducationType(@PathVariable String id) {
		return educationTypeService.read(id);
	}

	@RequestMapping(value = "educationTypes", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<EducationType> getEducationTypes() {
        Query query = new Query().with(new Sort(new Sort.Order(Sort.Direction.ASC, "name")));
		return educationTypeService.readMany(query);
	}

	// EducationType must contain the attribute 'type' that equals any string specified in EducationType  
	@RequestMapping(value = "educationTypes", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
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
