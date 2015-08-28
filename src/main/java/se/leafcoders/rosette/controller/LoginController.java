package se.leafcoders.rosette.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LoginController {

    @RequestMapping(value = "auth/bookings", method = RequestMethod.GET/*, produces = "application/json"*/)
    @ResponseBody
    public String authLogin(HttpServletResponse response) {
        return "Inloggad";
    }
}
