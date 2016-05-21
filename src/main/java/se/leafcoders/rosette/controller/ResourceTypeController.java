package se.leafcoders.rosette.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import se.leafcoders.rosette.model.resource.ResourceType;
import se.leafcoders.rosette.service.ResourceTypeService;
import se.leafcoders.rosette.util.ManyQuery;

@RestController
public class ResourceTypeController extends ApiV1Controller {
    @Autowired
    private ResourceTypeService resourceTypeService;

	@RequestMapping(value = "resourceTypes/{id}", method = RequestMethod.GET, produces = "application/json")
	public ResourceType getResourceType(@PathVariable String id) {
		return resourceTypeService.read(id);
	}

	@RequestMapping(value = "resourceTypes", method = RequestMethod.GET, produces = "application/json")
	public List<ResourceType> getResourceTypes(HttpServletRequest request, @RequestParam(required = false) String type) {
	    ManyQuery manyQuery = new ManyQuery(request, "name");
        if (type != null) {
            manyQuery.addCriteria(Criteria.where("type").is(type));
        }
		return resourceTypeService.readMany(manyQuery);
	}

	// ResourceType must contain the attribute 'type' that equals any string specified in ResourceType  
	@RequestMapping(value = "resourceTypes", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResourceType postResourceType(@RequestBody ResourceType resourceType, HttpServletResponse response) {
		return resourceTypeService.create(resourceType, response);
	}

	// ResourceType must contain the attribute 'type' that equals any string specified in ResourceType  
    @RequestMapping(value = "resourceTypes/{id}", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
    public void putResourceType(@PathVariable String id, HttpServletRequest request, HttpServletResponse response) {
		resourceTypeService.update(id, request, response);
    }

	@RequestMapping(value = "resourceTypes/{id}", method = RequestMethod.DELETE, produces = "application/json")
	public void deleteResourceType(@PathVariable String id, HttpServletResponse response) {
		resourceTypeService.delete(id, response);
	}
}
