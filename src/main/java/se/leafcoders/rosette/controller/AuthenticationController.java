package se.leafcoders.rosette.controller;

import javax.servlet.http.HttpServletResponse;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import se.leafcoders.rosette.model.User;
import se.leafcoders.rosette.service.SecurityService;

@Controller
public class AuthenticationController extends AbstractController {
	@Autowired
	private SecurityService securityService;
	
	public AuthenticationController() {
		super();
	}

	@RequestMapping(value = "authentication", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public User getAuthentication(HttpServletResponse response) {
		if ("".equals(SecurityUtils.getSubject().getPrincipal())) {
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			return null;
		} else {
			response.setStatus(HttpStatus.OK.value()); 
			return (User)SecurityUtils.getSubject().getPrincipal();
		}
	}
	
	@RequestMapping(value = "authCaches", method = RequestMethod.DELETE, produces = "application/json")
	public void clearAuthCaches(HttpServletResponse response) {
		securityService.resetPermissionCache();
		response.setStatus(HttpStatus.OK.value()); 
	}

}
