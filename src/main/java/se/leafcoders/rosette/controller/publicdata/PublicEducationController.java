package se.leafcoders.rosette.controller.publicdata;

import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import se.leafcoders.rosette.model.education.Education;
import se.leafcoders.rosette.model.education.EducationTheme;
import se.leafcoders.rosette.service.EducationService;
import se.leafcoders.rosette.service.EducationThemeService;
import se.leafcoders.rosette.util.ManyQuery;

@RestController
public class PublicEducationController extends PublicDataController {

	@Autowired
	private EducationService educationService;
    @Autowired
    private EducationThemeService educationThemeService;

	@RequestMapping(value = "educations/past", method = RequestMethod.GET, produces = "application/json")
	public List<Education> getPastEducations(HttpServletRequest request) {
		checkPermission();
        ManyQuery manyQuery = new ManyQuery(request, "-time");
        manyQuery.addCriteria(Criteria.where("time").lte(new Date()));
        return educationService.readMany(manyQuery, false);
	}	

	@RequestMapping(value = "educations/future", method = RequestMethod.GET, produces = "application/json")
	public List<Education> getFutureEducations(HttpServletRequest request) {
	    checkPermission();
        ManyQuery manyQuery = new ManyQuery(request, "-time");
        manyQuery.addCriteria(Criteria.where("time").gt(new Date()));
	    return educationService.readMany(manyQuery, false);
	}	
	
    @RequestMapping(value = "educationThemes", method = RequestMethod.GET, produces = "application/json")
    public List<EducationTheme> getEducationThemes(HttpServletRequest request) {
        checkPermission();
        return educationThemeService.readMany(new ManyQuery(request, "-id"), false);
    }   
}
