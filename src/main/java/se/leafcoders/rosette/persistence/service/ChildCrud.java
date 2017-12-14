package se.leafcoders.rosette.persistence.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import se.leafcoders.rosette.exception.ApiError;
import se.leafcoders.rosette.exception.ForbiddenException;
import se.leafcoders.rosette.exception.NotFoundException;
import se.leafcoders.rosette.permission.PermissionAction;
import se.leafcoders.rosette.permission.PermissionId;
import se.leafcoders.rosette.permission.PermissionValue;
import se.leafcoders.rosette.persistence.model.Persistable;

public abstract class ChildCrud<P extends Persistable, C extends Persistable, CIN> {

    private final PersistenceService<P, ?, ?> parentService;
    private final PersistenceService<C, CIN, ?> childService;
    private final CrudRepository<C, Long> childRepository;
    private final Class<P> parentClass;
    private final Class<C> childClass;
    private final String childPermission;

    public ChildCrud(PersistenceService<P, ?, ?> parentService, Class<P> parentClass, PersistenceService<C, CIN, ?> childService,
        CrudRepository<C, Long> childRepository, Class<C> childClass, String childPermission) {
        this.parentService = parentService;
        this.childService = childService;
        this.childRepository = childRepository;
        this.parentClass = parentClass;
        this.childClass = childClass;
        this.childPermission = childPermission;
    }

    private List<PermissionValue> parentItemPermissions(PermissionAction actionType, P parent) {
        List<PermissionValue> permissions = parentService.itemPermissions(PermissionAction.READ, new PermissionId<P>(parent));
        permissions.stream().forEach(permission -> permission.part(childPermission));
        return permissions;
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
        C itemInDb = childRepository.findOne(childId);
        if (itemInDb == null) {
            throw new NotFoundException(childClass.getSimpleName(), childId);
        }
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

        C child = childRepository.findOne(childId);
        if (child == null) {
            throw new NotFoundException(childClass.getSimpleName(), childId);
        }
        if (!getParentId(child).equals(parentId)) {
            throw ForbiddenException.dontBelongsTo(childClass, childId, parentClass, parentId);
        }
        childRepository.delete(childId);
        return ResponseEntity.noContent().build();
    }

    public abstract Long getParentId(C child);

    public abstract List<C> getChildren(P parent);

    public abstract void addChild(P parent, C child);
}
