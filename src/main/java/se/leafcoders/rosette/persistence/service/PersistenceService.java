package se.leafcoders.rosette.persistence.service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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
import se.leafcoders.rosette.persistence.repository.ModelRepository;
import se.leafcoders.rosette.service.SecurityService;
import se.leafcoders.rosette.util.ServerTime;

abstract class PersistenceService<T extends Persistable, IN, OUT> implements ServerTime {

    @Autowired
    SecurityService securityService;

    protected final Class<T> entityClass;
    protected final ModelRepository<T> repository;
    protected final Supplier<PermissionValue> permissionValueCreator;

    public PersistenceService(Class<T> entityClass, Supplier<PermissionValue> permissionValueCreator,
            ModelRepository<T> repository) {
        this.entityClass = entityClass;
        this.permissionValueCreator = permissionValueCreator;
        this.repository = repository;
    }

    // Override me to check more permissions for CREATE
    public List<PermissionValue> itemCreatePermissions(IN itemIn) {
        return Stream.of(permissionValueCreator.get().create()).collect(Collectors.toList());
    }

    // Override me to check more permissions for READ, UPDATE and DELETE
    public List<PermissionValue> itemReadUpdateDeletePermissions(PermissionAction actionType,
            PermissionId<T> permissionId) {
        return Stream.of(permissionValueCreator.get().action(actionType)
                .forId(permissionId != null ? permissionId.getId() : null)).collect(Collectors.toList());
    }

    public T create(IN itemIn, boolean checkPermissions) {
        return create(itemIn, checkPermissions, null);
    }

    public T create(IN itemIn, boolean checkPermissions, Consumer<T> beforeValidate) {
        if (checkPermissions) {
            checkPermissions(itemCreatePermissions(itemIn));
        }
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
        T item = repository.findById(id).orElseThrow(() -> notFoundException(id));
        if (checkPermissions) {
            checkPermissions(itemReadUpdateDeletePermissions(PermissionAction.READ, new PermissionId<T>(item)));
        }
        return item;
    }

    public List<T> readMany(boolean checkPermissions) {
        return readMany(null, checkPermissions);
    }

    public List<T> readMany(Sort sort, boolean checkPermissions) {
        sort = sort != null ? sort : new Sort(Sort.Direction.ASC, "id");
        return readManyCheckPermissions(repository.findAll(sort), checkPermissions);
    }

    public List<T> readMany(Specification<T> specification, Sort sort, boolean checkPermissions) {
        sort = sort != null ? sort : new Sort(Sort.Direction.ASC, "id");
        List<T> items = repository.findAll(specification, sort);
        return readManyCheckPermissions(items, checkPermissions);
    }

    public List<T> readMany(Specification<T> specification, Pageable pageable, boolean checkPermissions) {
        if (pageable.getSort() == null) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), new Sort(Sort.Direction.ASC, "id"));
        }
        Page<T> page = repository.findAll(specification, pageable);
        return readManyCheckPermissions(page.getContent(), checkPermissions);
    }
    
    protected List<T> readManyCheckPermissions(List<T> items, boolean checkPermissions) {
        return checkPermissions ? filterPermittedItems(items) : items;
    }

    public T update(Long id, Class<IN> inClass, HttpServletRequest request, boolean checkPermissions) {
        T itemInDb = repository.findById(id).orElseThrow(() -> notFoundException(id));
        checkPermissions(itemReadUpdateDeletePermissions(PermissionAction.UPDATE, new PermissionId<T>(itemInDb)));

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
        checkPermissions(itemReadUpdateDeletePermissions(PermissionAction.DELETE, new PermissionId<T>(id)));
        securityService.checkNotReferenced(id, entityClass);
        repository.deleteById(id);
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

    public final List<OUT> toOut(Collection<T> items) {
        return items != null ? items.stream().map(this::convertToOutDTO).collect(Collectors.toList()) : null;
    }

    protected final List<T> filterPermittedItems(Collection<T> items) {
        return items != null ? items.stream().filter(this::readManyItemFilter).collect(Collectors.toList()) : null;
    }

    public boolean readManyItemFilter(T item) {
        return isPermitted(itemReadUpdateDeletePermissions(PermissionAction.READ, new PermissionId<T>(item)));
    }

    protected void checkPermission(PermissionValue permission) {
        securityService.permissionResultFor(permission).checkAndThrow();
    }

    protected void checkAnyPermission(PermissionValue... permissions) {
        securityService.permissionResultFor(permissions).checkAndThrow();
    }

    protected boolean isPermitted(List<PermissionValue> permissions) {
        return securityService.permissionResultFor(permissions).isPermitted();
    }

    protected void checkPermissions(List<PermissionValue> permissions) {
        securityService.permissionResultFor(permissions).checkAndThrow();
    }

    public void checkPublicPermission() {
        checkPermission(permissionValueCreator.get().publicPermission());
    }

    public void checkPublicPermission(Long id) {
        checkPermission(permissionValueCreator.get().publicPermission().forId(id));
    }

    public NotFoundException notFoundException(Long id) {
        return new NotFoundException(entityClass.getSimpleName(), id);
    }
}
