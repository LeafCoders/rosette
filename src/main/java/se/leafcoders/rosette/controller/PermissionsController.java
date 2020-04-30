package se.leafcoders.rosette.controller;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

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

import lombok.RequiredArgsConstructor;
import se.leafcoders.rosette.controller.dto.PermissionIn;
import se.leafcoders.rosette.controller.dto.PermissionOut;
import se.leafcoders.rosette.persistence.service.PermissionService;

@RequiredArgsConstructor
@Transactional
@RestController
@RequestMapping(value = "api/permissions", produces = "application/json")
public class PermissionsController {

    private final PermissionService permissionService;

    @GetMapping(value = "/{id}")
    public PermissionOut getPermission(@PathVariable Long id) {
        return permissionService.toOut(permissionService.read(id, true));
    }

    @GetMapping
    public Collection<PermissionOut> getPermissions(HttpServletRequest request) {
        return permissionService.toOut(permissionService.readMany(true));
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<PermissionOut> postPermission(@RequestBody PermissionIn permission) {
        return new ResponseEntity<PermissionOut>(permissionService.toOut(permissionService.create(permission, true)),
                HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", consumes = "application/json")
    public PermissionOut putPermission(@PathVariable Long id, HttpServletRequest request) {
        return permissionService.toOut(permissionService.update(id, PermissionIn.class, request, true));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deletePermission(@PathVariable Long id) {
        return permissionService.delete(id, true);
    }
}
