package se.leafcoders.rosette.controller.auth;

import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ForgottenPasswordController extends AuthController {
/*
    @Autowired
    private ForgottenPasswordService forgottenPasswordService;

	@RequestMapping(value = "forgottenPassword", method = RequestMethod.POST, consumes = "application/json")
	public void createForgottenPassword(
        @RequestParam(value="email", required=true) String email,
        HttpServletResponse response
    ) {
	    forgottenPasswordService.create(email);
        response.setStatus(HttpServletResponse.SC_CREATED);
	}

    @RequestMapping(value = "forgottenPassword", method = RequestMethod.PUT, consumes = "application/json")
    public void applyForgottenPassword(
        @RequestParam(value="token", required=true) String token,
        @RequestParam(value="password", required=true) String password,
        HttpServletResponse response
    ) {
        forgottenPasswordService.apply(token, password);
        response.setStatus(HttpServletResponse.SC_OK);
    }
*/
}
