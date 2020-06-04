package se.leafcoders.rosette.endpoint.auth;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class ForgottenPasswordController extends AuthController {

    private final ForgottenPasswordService forgottenPasswordService;

    @PostMapping(value = "forgottenPassword")
    @ResponseStatus(HttpStatus.CREATED)
    public void createForgottenPassword(@RequestParam(required = true) String email) {
        forgottenPasswordService.create(email);
    }

    @PutMapping(value = "forgottenPassword")
    @ResponseStatus(HttpStatus.OK)
    public void applyForgottenPassword(@RequestParam(required = true) String token,
            @RequestParam(required = true) String password) {
        forgottenPasswordService.apply(token, password);
    }

}
