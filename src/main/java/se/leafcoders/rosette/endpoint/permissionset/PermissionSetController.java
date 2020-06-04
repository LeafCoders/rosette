package se.leafcoders.rosette.endpoint.permissionset;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.data.domain.Sort;
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

@RequiredArgsConstructor
@Transactional
@RestController
@RequestMapping(value = "api/permissionSets", produces = "application/json")
public class PermissionSetController {

    private final PermissionSetService permissionsetService;

    @GetMapping(value = "/{id}")
    public PermissionSetOut getPermissionSet(@PathVariable Long id) {
        return permissionsetService.toOut(permissionsetService.read(id, true));
    }

    @GetMapping
    public Collection<PermissionSetOut> getPermissionSets(HttpServletRequest request) {
        Sort sort = Sort.by("name").ascending();
        return permissionsetService.toOut(permissionsetService.readMany(sort, true));
    }

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public PermissionSetOut postPermissionSet(@RequestBody PermissionSetIn permissionset) {
        return permissionsetService.toOut(permissionsetService.create(permissionset, true));
    }

    @PutMapping(value = "/{id}", consumes = "application/json")
    public PermissionSetOut putPermissionSet(@PathVariable Long id, HttpServletRequest request) {
        return permissionsetService.toOut(permissionsetService.update(id, PermissionSetIn.class, request, true));
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePermissionSet(@PathVariable Long id) {
        permissionsetService.delete(id, true);
    }
}
