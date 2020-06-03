package se.leafcoders.rosette.controller.auth;

import javax.transaction.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Transactional
@RestController
@RequestMapping(value = "auth", produces = "application/json")
public abstract class AuthController {
}
