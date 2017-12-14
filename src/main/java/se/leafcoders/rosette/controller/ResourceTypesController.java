package se.leafcoders.rosette.controller;

import java.util.Collection;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import se.leafcoders.rosette.controller.dto.ResourceOut;
import se.leafcoders.rosette.controller.dto.ResourceTypeIn;
import se.leafcoders.rosette.controller.dto.ResourceTypeOut;
import se.leafcoders.rosette.persistence.service.ResourceService;
import se.leafcoders.rosette.persistence.service.ResourceTypeService;

@RestController
@RequestMapping(value = "api/resourceTypes", produces = "application/json")
public class ResourceTypesController {

    @Autowired
    private ResourceTypeService resourceTypeService;

    @Autowired
    private ResourceService resourceService;
    
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResourceTypeOut getResourceType(@PathVariable Long id) {
        return resourceTypeService.toOut(resourceTypeService.read(id, true));
    }

    @RequestMapping(method = RequestMethod.GET)
    public Collection<ResourceTypeOut> getResourceTypes(HttpServletRequest request) {
        Sort sort = new Sort(Sort.Direction.ASC, "name");        
        return resourceTypeService.toOut(resourceTypeService.readMany(sort, true));
    }

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<ResourceTypeOut> postResourceType(@RequestBody ResourceTypeIn resourceType) {
        return new ResponseEntity<ResourceTypeOut>(resourceTypeService.toOut(resourceTypeService.create(resourceType, true)), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = "application/json")
    public ResourceTypeOut putResourceType(@PathVariable Long id, HttpServletRequest request) {
        return resourceTypeService.toOut(resourceTypeService.update(id, ResourceTypeIn.class, request, true));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteResourceType(@PathVariable Long id) {
        return resourceTypeService.delete(id, true);
    }

    // Resources

    @RequestMapping(value = "/{id}/resources", method = RequestMethod.GET)
    public Collection<ResourceOut> getResourcesInResourceType(@PathVariable Long id) {
        return resourceService.toOut(resourceTypeService.readResources(id));
    }
}
