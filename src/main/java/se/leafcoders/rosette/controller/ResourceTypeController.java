package se.leafcoders.rosette.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import se.leafcoders.rosette.model.resource.ResourceType;
import se.leafcoders.rosette.service.ResourceTypeService;

@Controller
public class ResourceTypeController extends AbstractController {
    @Autowired
    private ResourceTypeService resourceTypeService;

	@RequestMapping(value = "resourceTypes/{id}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResourceType getResourceType(@PathVariable String id) {
		return resourceTypeService.read(id);
	}

	@RequestMapping(value = "resourceTypes", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<ResourceType> getResourceTypes(@RequestParam(required = false) String type) {
        Query query = new Query().with(new Sort(new Sort.Order(Sort.Direction.ASC, "name")));
        if (type != null) {
            query.addCriteria(Criteria.where("type").is(type));
        }
		return resourceTypeService.readMany(query);
	}

	// ResourceType must contain the attribute 'type' that equals any string specified in ResourceType  
	@RequestMapping(value = "resourceTypes", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
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
