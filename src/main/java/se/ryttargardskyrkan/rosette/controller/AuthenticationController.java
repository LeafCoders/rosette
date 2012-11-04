package se.ryttargardskyrkan.rosette.controller;

import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AuthenticationController extends AbstractController {
	public AuthenticationController() {
		super();
	}

	@RequestMapping(value = "authentication", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public String getAuthentication(HttpServletResponse response) {
		String responseBody = "";
		
		if ("".equals(SecurityUtils.getSubject().getPrincipal())) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			responseBody = "Unauthorized";
		} else {
			response.setStatus(HttpStatus.OK.value()); 
		}
		return responseBody;
	}

}
