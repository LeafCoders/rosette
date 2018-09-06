package se.leafcoders.rosette.controller;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import se.leafcoders.rosette.controller.dto.PermissionIn;
import se.leafcoders.rosette.controller.dto.PermissionOut;
import se.leafcoders.rosette.persistence.service.PermissionService;

@Transactional
@RestController
@RequestMapping(value = "api/permissions", produces = "application/json")
public class PermissionsController {

    @Autowired
    private PermissionService permissionService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public PermissionOut getPermission(@PathVariable Long id) {
        return permissionService.toOut(permissionService.read(id, true));
    }

    @RequestMapping(method = RequestMethod.GET)
    public Collection<PermissionOut> getPermissions(HttpServletRequest request) {
        return permissionService.toOut(permissionService.readMany(true));
    }

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<PermissionOut> postPermission(@RequestBody PermissionIn permission) {
        return new ResponseEntity<PermissionOut>(permissionService.toOut(permissionService.create(permission, true)), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = "application/json")
    public PermissionOut putPermission(@PathVariable Long id, HttpServletRequest request) {
        return permissionService.toOut(permissionService.update(id, PermissionIn.class, request, true));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deletePermission(@PathVariable Long id) {
        return permissionService.delete(id, true);
    }
}
