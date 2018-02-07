package se.leafcoders.rosette.persistence.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import se.leafcoders.rosette.exception.ApiError;
import se.leafcoders.rosette.exception.ForbiddenException;
import se.leafcoders.rosette.exception.NotFoundException;
import se.leafcoders.rosette.permission.PermissionAction;
import se.leafcoders.rosette.permission.PermissionId;
import se.leafcoders.rosette.permission.PermissionType;
import se.leafcoders.rosette.permission.PermissionValue;
import se.leafcoders.rosette.persistence.model.Persistable;
import se.leafcoders.rosette.persistence.repository.ModelRepository;
import se.leafcoders.rosette.service.SecurityService;

abstract class PersistenceService<T extends Persistable, IN, OUT> {

    @Autowired
    SecurityService securityService;

    protected final Class<T> entityClass;
    protected final ModelRepository<T> repository;
    protected final PermissionType permissionType;

    public PersistenceService(Class<T> entityClass, PermissionType permissionType, ModelRepository<T> repository) {
        this.entityClass = entityClass;
        this.permissionType = permissionType;
        this.repository = repository;
    }

    // Override me to check more permissions
    public List<PermissionValue> itemPermissions(PermissionAction actionType, PermissionId<T> permissionId) {
        return Arrays.asList(permissionValue(actionType).forId(permissionId != null ? permissionId.getId() : null));
    }

    protected List<PermissionValue> itemPermissions(PermissionAction actionType) {
        return itemPermissions(actionType, new PermissionId<T>());
    }
    
    public T create(IN itemIn, boolean checkPermissions) {
        return create(itemIn, checkPermissions, null);
    }

    public T create(IN itemIn, boolean checkPermissions, Consumer<T> beforeValidate) {
        checkPermissions(itemPermissions(PermissionAction.CREATE));
        securityService.validate(itemIn, null);
        T item = fromIn(itemIn);
        if (beforeValidate != null) {
            beforeValidate.accept(item);
        }
        securityService.validate(item, null);
        extraValidation(item, itemIn);
        try {
            return repository.save(item);
        } catch (org.springframework.dao.DuplicateKeyException ignore) {
            throw new ForbiddenException(ApiError.CREATE_ALREADY_EXIST);
        } catch (org.springframework.dao.DataIntegrityViolationException exception) {
            throw exception;
        }
    }

    public T read(Long id, boolean checkPermissions) {
        if (id == null) {
            return null;
        }
        T item = repository.findOne(id);
        if (item == null) {
            throw notFoundException(id);
        }
        if (checkPermissions) {
            checkPermissions(itemPermissions(PermissionAction.READ, new PermissionId<T>(item)));
        }
        return item;
    }

    public List<T> readMany(boolean checkPermissions) {
        return readMany(null, checkPermissions);
    }

    public List<T> readMany(Sort sort, boolean checkPermissions) {
        return readManyCheckPermissions(repository.findAll(sort), checkPermissions);
    }
    
    protected List<T> readManyCheckPermissions(Iterable<T> items, boolean checkPermissions) {
        if (checkPermissions) {
            return filterPermittedItems(items/* , manyQuery */);
        } else {
            List<T> result = new LinkedList<T>();
            for (T item : items) {
                result.add(item);
            }
            return result; // manyQuery.filter(items);
        }
    }

    public T update(Long id, Class<IN> inClass, HttpServletRequest request, boolean checkPermissions) {
        T itemInDb = repository.findOne(id);
        if (itemInDb == null) {
            throw notFoundException(id);
        }
        checkPermissions(itemPermissions(PermissionAction.UPDATE, new PermissionId<T>(itemInDb)));

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rawData = objectMapper.readTree(request.getReader());
            IN itemIn = objectMapper.treeToValue(rawData, inClass);


// Kanske inte funkar med denna????            securityService.validate(itemIn, rawData);
            fromIn(itemIn, rawData, itemInDb);
            securityService.validate(itemInDb, null);
            extraValidation(itemInDb, itemIn);
            return repository.save(itemInDb);
        } catch (JsonProcessingException exception) {
            // TODO: Invalid content might cause this exception
            exception.printStackTrace(System.err);
            throw notFoundException(id);
        } catch (IOException exception) {
            exception.printStackTrace(System.err);
            throw notFoundException(id);
        }
    }

    public ResponseEntity<Void> delete(Long id, boolean checkPermissions) {
        checkPermissions(itemPermissions(PermissionAction.DELETE, new PermissionId<T>(id)));
        securityService.checkNotReferenced(id, entityClass);
        repository.delete(id);
        return ResponseEntity.noContent().build();
    }

    protected void extraValidation(T itemToValidate, IN itemChanges) {}
    
    protected abstract T convertFromInDTO(IN itemIn, JsonNode rawIn, T itemToUpdate);

    protected abstract OUT convertToOutDTO(T item);

    public T fromIn(IN itemIn) {
        try {
            return itemIn != null ? fromIn(itemIn, null, entityClass.newInstance()) : null;
        } catch (InstantiationException ignore) {
        } catch (IllegalAccessException ignore) {
        }
        // TOOD: Throw some internal exception 500 when newInstance() throws or
        // when itemIn is null
        return null;
    }

    public T fromIn(IN itemIn, JsonNode rawIn, T itemToUpdate) {
        return itemIn != null ? convertFromInDTO(itemIn, rawIn, itemToUpdate) : null;
    }

    public OUT toOut(T item) {
        return item != null ? convertToOutDTO(item) : null;
    }

    public <REF> REF toOutRef(T item, Function<T, REF> toRef) {
        return item != null ? toRef.apply(item) : null;
    }
    
    public List<OUT> toOut(List<T> items) {
        return items != null ? items.stream().map(item -> convertToOutDTO(item)).collect(Collectors.toList()) : null;
    }

    protected List<T> filterPermittedItems(
        Iterable<T> items/* , ManyQuery manyQuery */) {
        List<T> result = new LinkedList<T>();
        if (items != null) {
            // int skippedItems = 0;
            for (T item : items) {
                if (readManyItemFilter(item)) {
                    // if (skippedItems >= manyQuery.getStartIndex()) {
                    result.add(item);
                    // } else {
                    // skippedItems++;
                    // }
                }
                // if (result.size() >= manyQuery.getMaxItems()) {
                // return result;
                // }
            }
        }
        return result;
    }

    public boolean readManyItemFilter(T item) {
        return isPermitted(itemPermissions(PermissionAction.READ, new PermissionId<T>(item)));
    }

    protected PermissionValue permissionValue(PermissionType type, PermissionAction actionType) {
        return new PermissionValue(type, actionType);
    }

    protected PermissionValue permissionValue(PermissionAction actionType) {
        return new PermissionValue(permissionType, actionType);
    }
    
    protected void checkPermission(PermissionValue permission) {
        securityService.permissionResultFor(permission).checkAndThrow();
    }

    protected void checkAnyPermission(PermissionValue... permissions) {
        securityService.permissionResultFor(permissions).checkAndThrow();
    }

    protected boolean isPermitted(List<PermissionValue> permissions) {
        return securityService.permissionResultFor((PermissionValue[]) permissions.toArray()).isPermitted();
    }

    protected void checkPermissions(List<PermissionValue> permissions) {
        securityService.permissionResultFor((PermissionValue[]) permissions.toArray()).checkAndThrow();
    }
    
    public void checkPublicPermission(Long id) {
        checkPermission(permissionValue(PermissionAction.PUBLIC).forId(id));
    }

    public NotFoundException notFoundException(Long id) {
        return new NotFoundException(entityClass.getSimpleName(), id);
    }
}
