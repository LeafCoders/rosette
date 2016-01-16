package se.leafcoders.rosette.controller;

import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import se.leafcoders.rosette.service.SecurityService;

@Profile("development")
@Controller
public class DevelopmentController extends AbstractController {
	@Autowired
	private SecurityService security;

    @RequestMapping(value = "development/resetPermissionCache", method = RequestMethod.DELETE, produces = "application/json")
    public void resetPermissionCache(HttpServletResponse response) {
        security.resetPermissionCache();
    }
}
