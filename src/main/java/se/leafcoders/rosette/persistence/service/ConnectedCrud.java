package se.leafcoders.rosette.persistence.service;

import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.repository.CrudRepository;
import se.leafcoders.rosette.exception.ApiError;
import se.leafcoders.rosette.exception.ForbiddenException;
import se.leafcoders.rosette.persistence.model.Persistable;

public abstract class ConnectedCrud<P extends Persistable, C extends Persistable> {

    private final PersistenceService<P, ?, ?> parentService;
    private final PersistenceService<C, ?, ?> childService;
    private final CrudRepository<P, Long> parentRepository;

    public ConnectedCrud(
        PersistenceService<P, ?, ?> parentService,
        PersistenceService<C, ?, ?> childService,
        CrudRepository<P, Long> parentRepository
    ) {
        this.parentService = parentService;
        this.parentRepository = parentRepository;
        this.childService = childService;
    }

    public List<C> readAll(Long parentId) {
        return readChildren(parentService.read(parentId, true));
    }

    public List<C> connect(Long parentId, Long childId) {
        parentService.checkPermission(parentService.permissionValueCreator.get().update().forId(parentId));
        P parent = parentService.read(parentId, true);
        C child = childService.read(childId, true);
        addChild(parent, child);
        try {
            return readChildren(parentRepository.save(parent));
        } catch (DataIntegrityViolationException ignore) {
            throw new ForbiddenException(ApiError.CHILD_ALREADY_EXIST);
        }
    }

    public List<C> disconnect(Long parentId, Long childId) {
        parentService.checkPermission(parentService.permissionValueCreator.get().update().forId(parentId));
        P parent = parentService.read(parentId, true);
        C child = childService.read(childId, true);
        removeChild(parent, child);
        return readChildren(parentRepository.save(parent));
    }

    protected abstract void addChild(P parent, C child);

    protected abstract void removeChild(P parent, C child);

    protected abstract List<C> readChildren(P parent);

}
