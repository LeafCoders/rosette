package se.leafcoders.rosette.controller;

import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import se.leafcoders.rosette.model.resource.Resource;
import se.leafcoders.rosette.service.EventResourceService;

@RestController
public class EventResourceController extends ApiV1Controller {
	@Autowired
	private EventResourceService eventResourceService;

    @RequestMapping(value = "events/{eventId}/resources", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public void postResource(@PathVariable String eventId, @RequestBody Resource resource, HttpServletResponse response) {
        eventResourceService.addResource(eventId, resource, response);
    }

	@RequestMapping(value = "events/{eventId}/resources/{resourceTypeId}", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	public void putResource(@PathVariable String eventId, @PathVariable String resourceTypeId, @RequestBody Resource resource, HttpServletResponse response) {
		eventResourceService.updateResource(eventId, resourceTypeId, resource, response);
	}

    @RequestMapping(value = "events/{eventId}/resources/{resourceTypeId}", method = RequestMethod.DELETE, produces = "application/json")
    public void deleteResource(@PathVariable String eventId, @PathVariable String resourceTypeId, HttpServletResponse response) {
        eventResourceService.removeResource(eventId, resourceTypeId, response);
    }
}
