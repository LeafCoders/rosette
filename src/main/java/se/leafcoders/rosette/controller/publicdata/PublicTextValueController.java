package se.leafcoders.rosette.controller.publicdata;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import se.leafcoders.rosette.exception.NotFoundException;
import se.leafcoders.rosette.model.TextValue;
import se.leafcoders.rosette.service.TextValueService;
import se.leafcoders.rosette.util.ManyQuery;

@RestController
public class PublicTextValueController extends PublicDataController {

	@Autowired
	private TextValueService textValueService;

    @RequestMapping(value = "textValues/{id}", method = RequestMethod.GET, produces = "application/json")
    public TextValue getTextValue(@PathVariable String id) {
        checkPermission();

        TextValue textValue = textValueService.read(id, false);
        if (textValue == null || !textValue.getIsPublic()) {
            throw new NotFoundException(TextValue.class.getSimpleName(), id);
        }
        return textValue;
    }   

    @RequestMapping(value = "textValues", method = RequestMethod.GET, produces = "application/json")
	public List<TextValue> getTextValues(HttpServletRequest request) {
		checkPermission();

		ManyQuery manyQuery = new ManyQuery(request);
		manyQuery.addCriteria(Criteria.where("isPublic").is(Boolean.TRUE));

		List<TextValue> textValues = textValueService.readMany(new ManyQuery(request), false);
		return textValues;
	}
}
