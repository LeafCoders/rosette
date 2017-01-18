package se.leafcoders.rosette.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import se.leafcoders.rosette.model.TextValue;
import se.leafcoders.rosette.service.TextValueService;
import se.leafcoders.rosette.util.ManyQuery;

@RestController
public class TextValueController extends ApiV1Controller {

    @Autowired
	private TextValueService textValueService;

	@RequestMapping(value = "textValues/{id}", method = RequestMethod.GET, produces = "application/json")
	public TextValue getTextValue(@PathVariable String id) {
		return textValueService.read(id);
	}

	@RequestMapping(value = "textValues", method = RequestMethod.GET, produces = "application/json")
	public List<TextValue> getTextValues(HttpServletRequest request) {
		return textValueService.readMany(new ManyQuery(request));
	}

    @RequestMapping(value = "textValues", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public TextValue postTextValue(@RequestBody TextValue textValue, HttpServletResponse response) {
        return textValueService.create(textValue, response);
    }

	@RequestMapping(value = "textValues/{id}", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	public void putTextValue(@PathVariable String id, HttpServletRequest request, HttpServletResponse response) {
		textValueService.update(id, request, response);
	}

	@RequestMapping(value = "textValues/{id}", method = RequestMethod.DELETE, produces = "application/json")
	public void deleteTextValues(@PathVariable String id, HttpServletResponse response) {
		textValueService.delete(id, response);
	}
}
