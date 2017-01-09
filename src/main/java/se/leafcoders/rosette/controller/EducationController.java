package se.leafcoders.rosette.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import se.leafcoders.rosette.model.education.Education;
import se.leafcoders.rosette.service.EducationService;
import se.leafcoders.rosette.util.ManyQuery;

@RestController
public class EducationController extends ApiV1Controller {
    @Autowired
    private EducationService educationService;

	@RequestMapping(value = "educations/{id}", method = RequestMethod.GET, produces = "application/json")
	public Education getEducation(@PathVariable String id) {
		return educationService.read(id);
	}

	@RequestMapping(value = "educations", method = RequestMethod.GET, produces = "application/json")
	public List<Education> getEducations(
            @RequestParam(required = false) String educationTypeId,
            @RequestParam(required = false) String eventId,
            HttpServletRequest request
    ) {
        ManyQuery manyQuery = new ManyQuery(request, "-time");
        if (educationTypeId != null) {
            manyQuery.addCriteria(Criteria.where("educationType._id").is(new ObjectId(educationTypeId)));
        }
        if (eventId != null) {
            manyQuery.addCriteria(Criteria.where("event._id").is(new ObjectId(eventId)));
        }
		return educationService.readMany(manyQuery);
	}

	// Education must contain the attribute 'type' that equals any string specified in Education  
	@RequestMapping(value = "educations", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
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
