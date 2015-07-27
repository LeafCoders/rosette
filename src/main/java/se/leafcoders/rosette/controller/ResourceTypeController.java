package se.leafcoders.rosette.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import se.leafcoders.rosette.model.resource.ResourceType;
import se.leafcoders.rosette.service.ResourceTypeService;

@Controller
public class ResourceTypeController extends AbstractController {
    @Autowired
    private ResourceTypeService resourceTypeService;

	@RequestMapping(value = "resourceTypes/{key}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResourceType getResourceType(@PathVariable String key) {
		return resourceTypeService.read(key);
	}

	@RequestMapping(value = "resourceTypes", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<ResourceType> getResourceTypes(@RequestParam(value = "groupId", required = false) String groupId, HttpServletRequest request, HttpServletResponse response) {
        Query query = new Query().with(new Sort(new Sort.Order(Sort.Direction.ASC, "name")));
		return resourceTypeService.readMany(query);
	}

	// ResourceType must contain the attribute 'type' that equals any string specified in ResourceType  
	@RequestMapping(value = "resourceTypes", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResourceType postResourceType(@RequestBody ResourceType resourceType, HttpServletResponse response) {
		return resourceTypeService.create(resourceType, response);
	}

	// ResourceType must contain the attribute 'type' that equals any string specified in ResourceType  
    @RequestMapping(value = "resourceTypes/{key}", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
    public void putResourceType(@PathVariable String key, HttpServletRequest request, HttpServletResponse response) {
		resourceTypeService.update(key, request, response);
    }

	@RequestMapping(value = "resourceTypes/{key}", method = RequestMethod.DELETE, produces = "application/json")
	public void deleteResourceType(@PathVariable String key, HttpServletResponse response) {
		resourceTypeService.delete(key, response);
	}
}
