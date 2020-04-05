package se.leafcoders.rosette.controller;

import java.util.Collection;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.leafcoders.rosette.controller.dto.ResourceOut;
import se.leafcoders.rosette.controller.dto.ResourceTypeIn;
import se.leafcoders.rosette.controller.dto.ResourceTypeOut;
import se.leafcoders.rosette.persistence.service.ResourceService;
import se.leafcoders.rosette.persistence.service.ResourceTypeService;

@Transactional
@RestController
@RequestMapping(value = "api/resourceTypes", produces = "application/json")
public class ResourceTypesController {

    @Autowired
    private ResourceTypeService resourceTypeService;

    @Autowired
    private ResourceService resourceService;
    
    @GetMapping(value = "/{id}")
    public ResourceTypeOut getResourceType(@PathVariable Long id) {
        return resourceTypeService.toOut(resourceTypeService.read(id, true));
    }

    @GetMapping
    public Collection<ResourceTypeOut> getResourceTypes(HttpServletRequest request) {
        return readAll();
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<ResourceTypeOut> postResourceType(@RequestBody ResourceTypeIn resourceType) {
        return new ResponseEntity<ResourceTypeOut>(resourceTypeService.toOut(resourceTypeService.create(resourceType, true)), HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", consumes = "application/json")
    public ResourceTypeOut putResourceType(@PathVariable Long id, HttpServletRequest request) {
        return resourceTypeService.toOut(resourceTypeService.update(id, ResourceTypeIn.class, request, true));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteResourceType(@PathVariable Long id) {
        return resourceTypeService.delete(id, true);
    }
    
    @PutMapping(value = "/{id}/moveTo/{toResourceTypeId}", consumes = "application/json")
    public Collection<ResourceTypeOut> moveResourceType(@PathVariable Long id, @PathVariable Long toResourceTypeId) {
        resourceTypeService.moveResourceType(id, toResourceTypeId);
        return readAll();
    }

    private Collection<ResourceTypeOut> readAll() {
        Sort sort = new Sort(Sort.Direction.ASC, "displayOrder");
        return resourceTypeService.toOut(resourceTypeService.readMany(sort, true));
    }
    
    // Resources

    @GetMapping(value = "/{id}/resources")
    public Collection<ResourceOut> getResourcesInResourceType(@PathVariable Long id) {
        return resourceService.toOut(resourceTypeService.readResources(id));
    }
}
