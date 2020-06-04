package se.leafcoders.rosette.endpoint.permission;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import se.leafcoders.rosette.endpoint.permissionset.PermissionSetOut;
import se.leafcoders.rosette.endpoint.permissionset.PermissionSetService;

@RequiredArgsConstructor
@Transactional
@RestController
@RequestMapping(value = "api/permissions", produces = "application/json")
public class PermissionController {

    private final PermissionService permissionService;
    private final PermissionSetService permissionSetService;

    @GetMapping(value = "/{id}")
    public PermissionOut getPermission(@PathVariable Long id) {
        return permissionService.toOut(permissionService.read(id, true));
    }

    @GetMapping
    public Collection<PermissionOut> getPermissions(HttpServletRequest request) {
        return permissionService.toOut(permissionService.readMany(true));
    }

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public PermissionOut postPermission(@RequestBody PermissionIn permission) {
        return permissionService.toOut(permissionService.create(permission, true));
    }

    @PutMapping(value = "/{id}", consumes = "application/json")
    public PermissionOut putPermission(@PathVariable Long id, HttpServletRequest request) {
        return permissionService.toOut(permissionService.update(id, PermissionIn.class, request, true));
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePermission(@PathVariable Long id) {
        permissionService.delete(id, true);
    }

    // PermissionSet

    @GetMapping(value = "/{id}/permissionSets")
    public Collection<PermissionSetOut> getPermissionSetOfPermission(@PathVariable Long id) {
        return permissionSetService.toOut(permissionService.getPermissionSets(id));
    }

    @PostMapping(value = "/{id}/permissionSets/{permissionSetId}", consumes = "application/json")
    public Collection<PermissionSetOut> addPermissionSetToPermission(@PathVariable Long id,
            @PathVariable Long permissionSetId) {
        return permissionSetService.toOut(permissionService.addPermissionSet(id, permissionSetId));
    }

    @DeleteMapping(value = "/{id}/permissionSets/{permissionSetId}")
    public Collection<PermissionSetOut> removePermissionSetFromPermission(@PathVariable Long id,
            @PathVariable Long permissionSetId) {
        return permissionSetService.toOut(permissionService.removePermissionSet(id, permissionSetId));
    }
}
