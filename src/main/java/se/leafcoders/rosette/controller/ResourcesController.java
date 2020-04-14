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
import se.leafcoders.rosette.controller.dto.ResourceIn;
import se.leafcoders.rosette.controller.dto.ResourceOut;
import se.leafcoders.rosette.controller.dto.ResourceTypeOut;
import se.leafcoders.rosette.persistence.service.ResourceService;
import se.leafcoders.rosette.persistence.service.ResourceTypeService;

@Transactional
@RestController
@RequestMapping(value = "api/resources", produces = "application/json")
public class ResourcesController {

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private ResourceTypeService resourceTypeService;

    @GetMapping(value = "/{id}")
    public ResourceOut getResource(@PathVariable Long id) {
        return resourceService.toOut(resourceService.read(id, true));
    }

    @GetMapping
    public Collection<ResourceOut> getResources(HttpServletRequest request) {
        Sort sort = Sort.by("lastUseTime").descending();
        return resourceService.toOut(resourceService.readMany(sort, true));
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<ResourceOut> postResource(@RequestBody ResourceIn resource) {
        return new ResponseEntity<ResourceOut>(resourceService.toOut(resourceService.create(resource, true)), HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", consumes = "application/json")
    public ResourceOut putResource(@PathVariable Long id, HttpServletRequest request) {
        return resourceService.toOut(resourceService.update(id, ResourceIn.class, request, true));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteResource(@PathVariable Long id) {
        return resourceService.delete(id, true);
    }

    // ResourceTypes
    
    @GetMapping(value = "/{id}/resourceTypes")
    public Collection<ResourceTypeOut> getResourceTypesOfResource(@PathVariable Long id) {
        return resourceTypeService.toOut(resourceService.getResourceTypes(id));
    }

    @PostMapping(value = "/{id}/resourceTypes/{resourceTypeId}", consumes = "application/json")
    public Collection<ResourceTypeOut> addResourceTypeToResource(@PathVariable Long id, @PathVariable Long resourceTypeId) {
        return resourceTypeService.toOut(resourceService.addResourceType(id, resourceTypeId));
    }

    @DeleteMapping(value = "/{id}/resourceTypes/{resourceTypeId}")
    public Collection<ResourceTypeOut> removeResourceTypeFromResource(@PathVariable Long id, @PathVariable Long resourceTypeId) {
        return resourceTypeService.toOut(resourceService.removeResourceType(id, resourceTypeId));
    }
}
