package se.leafcoders.rosette.controller.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import se.leafcoders.rosette.persistence.service.ForgottenPasswordService;

@RestController
public class ForgottenPasswordController extends AuthController {

    @Autowired
    private ForgottenPasswordService forgottenPasswordService;

	@PostMapping(value = "forgottenPassword")
	public ResponseEntity<Void> createForgottenPassword(@RequestParam(required = true) String email) {
	    forgottenPasswordService.create(email);
	    return new ResponseEntity<Void>(HttpStatus.CREATED);
	}

    @PutMapping(value = "forgottenPassword")
    public ResponseEntity<Void> applyForgottenPassword(@RequestParam(required = true) String token, @RequestParam(required = true) String password) {
        forgottenPasswordService.apply(token, password);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

}
