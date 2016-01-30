package se.leafcoders.rosette.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import se.leafcoders.rosette.config.RosetteSettings;

/**
 * This controller will be called when no controller match the requested url 
 */
@Controller
public class CatchErrorController implements ErrorController {

    private static final String PATH = "/error";

    @Autowired
    private RosetteSettings rosetteSettings;

	@RequestMapping(value = PATH, produces = "text/html")
	@ResponseBody
	public String error() {
		return "<h1>Rosette server</h1><p>No resource here. You should try <pre>" + rosetteSettings.getBaseUrl()  + "/api/" + rosetteSettings.getApiVersion() + "/...</pre></p>";
	}

    @Override
    public String getErrorPath() {
        return PATH;
    }
}
