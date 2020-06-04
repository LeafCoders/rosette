package se.leafcoders.rosette.core.persistable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.data.repository.CrudRepository;
import org.springframework.http.ResponseEntity;

import se.leafcoders.rosette.core.exception.ApiError;
import se.leafcoders.rosette.core.exception.ForbiddenException;
import se.leafcoders.rosette.core.exception.NotFoundException;
import se.leafcoders.rosette.core.permission.PermissionAction;
import se.leafcoders.rosette.core.permission.PermissionValue;

public abstract class ChildCrud<P extends Persistable, C extends Persistable, CIN> {

    private final PersistenceService<P, ?, ?> parentService;
    private final PersistenceService<C, CIN, ?> childService;
    private final CrudRepository<C, Long> childRepository;
    private final Class<P> parentClass;
    private final Class<C> childClass;

    public ChildCrud(PersistenceService<P, ?, ?> parentService, Class<P> parentClass,
            PersistenceService<C, CIN, ?> childService,
            CrudRepository<C, Long> childRepository, Class<C> childClass) {
        this.parentService = parentService;
        this.childService = childService;
        this.childRepository = childRepository;
        this.parentClass = parentClass;
        this.childClass = childClass;
    }

    private List<PermissionValue> parentItemPermissions(PermissionAction action, P parent) {
        return getPermissionValuesForAction(action, parent);
    }

    public List<C> readAll(Long parentId) {
        P parent = parentService.read(parentId, false);
        if (parentService.isPermitted(parentItemPermissions(PermissionAction.READ, parent))) {
            return getChildren(parent);
        }
        return new ArrayList<C>();
    }

    public C add(Long parentId, CIN childIn) {
        P parent = parentService.read(parentId, false);

        parentService.checkPermissions(parentItemPermissions(PermissionAction.CREATE, parent));

        childService.securityService.validate(childIn, null);
        C child = childService.fromIn(childIn);
        addChild(parent, child);
        parentService.securityService.validate(child, null);

        try {
            return childRepository.save(child);
        } catch (org.springframework.dao.DuplicateKeyException ignore) {
            throw new ForbiddenException(ApiError.CREATE_ALREADY_EXIST, child.getId().toString());
        }
    }

    public C update(Long parentId, Long childId, Class<CIN> itemInClass, HttpServletRequest request) {
        C itemInDb = childRepository.findById(childId).orElseThrow(() -> {
            return new NotFoundException(childClass.getSimpleName(), childId);
        });
        if (!getParentId(itemInDb).equals(parentId)) {
            throw ForbiddenException.dontBelongsTo(childClass, childId, parentClass, parentId);
        }
        P parent = parentService.read(parentId, false);
        parentService.checkPermissions(parentItemPermissions(PermissionAction.UPDATE, parent));

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rawData = objectMapper.readTree(request.getReader());
            CIN itemIn = objectMapper.treeToValue(rawData, itemInClass);

            parentService.securityService.validate(itemIn, rawData);
            childService.fromIn(itemIn, rawData, itemInDb);
            childService.securityService.validate(itemInDb, null);
            return childRepository.save(itemInDb);
        } catch (JsonProcessingException exception) {
            // TODO: Invalid content might cause this exception
            exception.printStackTrace(System.err);
            throw new NotFoundException(childClass.getSimpleName(), childId);
        } catch (IOException exception) {
            exception.printStackTrace(System.err);
            throw new NotFoundException(childClass.getSimpleName(), childId);
        }
    }

    public ResponseEntity<Void> delete(Long parentId, Long childId) {
        P parent = parentService.read(parentId, false);
        parentService.checkPermissions(parentItemPermissions(PermissionAction.DELETE, parent));
        // securityService.checkNotReferenced(id, entityClass);

        C child = childRepository.findById(childId).orElseThrow(() -> {
            return new NotFoundException(childClass.getSimpleName(), childId);
        });
        if (!getParentId(child).equals(parentId)) {
            throw ForbiddenException.dontBelongsTo(childClass, childId, parentClass, parentId);
        }
        childRepository.deleteById(childId);
        return ResponseEntity.noContent().build();
    }

    public abstract List<PermissionValue> getPermissionValuesForAction(PermissionAction action, P parent);

    public abstract Long getParentId(C child);

    public abstract List<C> getChildren(P parent);

    public abstract void addChild(P parent, C child);
}
