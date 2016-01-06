package se.leafcoders.rosette.controller.publicdata;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import se.leafcoders.rosette.model.education.Education;
import se.leafcoders.rosette.model.education.EducationTheme;
import se.leafcoders.rosette.service.EducationService;
import se.leafcoders.rosette.service.EducationThemeService;
import se.leafcoders.rosette.util.ManyQuery;

@Controller
public class PublicEducationController extends PublicDataController {

	@Autowired
	private EducationService educationService;
    @Autowired
    private EducationThemeService educationThemeService;

	@RequestMapping(value = "educations", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<Education> getEducations(HttpServletRequest request) {
		checkPermission();
		return educationService.readMany(new ManyQuery(request, "-displayTime"), false);
	}	

    @RequestMapping(value = "educationThemes", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public List<EducationTheme> getEducationThemes(HttpServletRequest request) {
        checkPermission();
        return educationThemeService.readMany(new ManyQuery(request, "-id"), false);
    }   
}
