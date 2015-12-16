package se.leafcoders.rosette.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import se.leafcoders.rosette.exception.NotFoundException;
import se.leafcoders.rosette.exception.SimpleValidationException;
import se.leafcoders.rosette.model.BaseModel;
import se.leafcoders.rosette.model.error.ValidationError;
import se.leafcoders.rosette.security.PermissionAction;
import se.leafcoders.rosette.security.PermissionCheckFilter;
import se.leafcoders.rosette.security.PermissionType;
import se.leafcoders.rosette.security.PermissionValue;
import se.leafcoders.rosette.util.QueryId;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

abstract class MongoTemplateCRUD<T extends BaseModel> implements StandardCRUD<T> {
	@Autowired
	protected MongoTemplate mongoTemplate;
	@Autowired
	protected SecurityService security;
    @Autowired
    protected RefreshService refreshService;

	protected ObjectMapper objectMapper = new ObjectMapper();

	protected final PermissionType permissionType;
	protected final PermissionCheckFilter permissionFilter;
	private final Class<T> entityClass;

	public MongoTemplateCRUD(Class<T> entityClass, PermissionType permissionType) {
		this(entityClass, permissionType, PermissionCheckFilter.ALL);
	}

	public MongoTemplateCRUD(Class<T> entityClass, PermissionType permissionType, PermissionCheckFilter permissionFilter) {
		this.permissionType = permissionType;
		this.entityClass = entityClass;
		this.permissionFilter = permissionFilter;
	}

	protected void checkPermission(PermissionAction actionType) {
		security.checkPermission(new PermissionValue(permissionType, actionType));
	}

	protected void checkPermission(PermissionAction actionType, String id) {
		security.checkPermission(new PermissionValue(permissionType, actionType, id));
	}

	protected boolean isPermitted(PermissionAction accessType) {
		return security.isPermitted(new PermissionValue(permissionType, accessType));
	}

	protected boolean isPermitted(PermissionAction accessType, String id) {
		return security.isPermitted(new PermissionValue(permissionType, accessType, id));
	}

	protected Query getIdQuery(String id) {
		return Query.query(Criteria.where("id").is(QueryId.get(id)));
	}

	@Override
	public T create(T data, HttpServletResponse response) {
		if (permissionFilter.shallCheck(PermissionAction.CREATE)) {
			checkPermission(PermissionAction.CREATE);
		}
		setReferences(data, true);
        afterSetReferences(data, null, true);
		security.validate(data);
		mongoTemplate.insert(data);
		response.setStatus(HttpStatus.CREATED.value());
		return data;
	}

    @Override
    public T read(String id) {
        return this.read(id, true);
    }

	@Override
	public T read(String id, boolean checkPermissions) {
		if (checkPermissions && permissionFilter.shallCheck(PermissionAction.READ)) {
			checkPermission(PermissionAction.READ, id);
		}

		T data = mongoTemplate.findById(id, entityClass);
        if (data == null) {
            throw notFoundException(id);
        }
        return data;
	}

    @Override
    public List<T> readMany(final Query query) {
        return this.readMany(query, true);
    }

	@Override
	public List<T> readMany(final Query query, boolean checkPermissions) {
		List<T> items = mongoTemplate.find(query, entityClass);
		return checkPermissions ? filterPermittedItems(items) : items;
	}

	@Override
	public void update(String id, HttpServletRequest request, HttpServletResponse response) {
		if (permissionFilter.shallCheck(PermissionAction.UPDATE)) {
			checkPermission(PermissionAction.UPDATE, id);
		}
		T dataInDbToUpdate = read(id);
		if (dataInDbToUpdate == null) {
			throw notFoundException(id);
		}

		JsonNode rawData = null;
		T updateData = null;
		try {
			rawData = objectMapper.readTree(request.getReader());
			updateData = objectMapper.treeToValue(rawData, entityClass);
		} catch (Exception ignore) {
			throw notFoundException(id);
		}

		beforeUpdate(id, updateData, dataInDbToUpdate);
		setReferences(updateData, true);
		afterSetReferences(updateData, dataInDbToUpdate, true);
		dataInDbToUpdate.update(rawData, updateData);
		security.validate(dataInDbToUpdate);
		mongoTemplate.save(dataInDbToUpdate);
		response.setStatus(HttpStatus.OK.value());
		refreshService.setNeedRefresh(entityClass.getSimpleName());
	}

	protected void beforeUpdate(String id, T updateData, T dataInDbToUpdate) {
	}
	
    protected void afterSetReferences(T updateData, T dataInDbToUpdate, boolean checkPermissions) {
    }

	@Override
	public void delete(String id, HttpServletResponse response) {
		if (permissionFilter.shallCheck(PermissionAction.DELETE)) {
			checkPermission(PermissionAction.DELETE, id);
		}
		security.checkNotReferenced(id, permissionType);
        if (mongoTemplate.findAndRemove(getIdQuery(id), entityClass) == null) {
			throw notFoundException(id);
		}
		response.setStatus(HttpStatus.OK.value());
	}
	
    @Override
    public void refresh(Set<String> changedCollections) {
        for (Class<?> refClass : references()) {
            if (changedCollections.contains(refClass.getSimpleName())) {
                List<T> items = readMany(new Query(), false);
                items.forEach((T data) -> {
                    beforeUpdate(data.getId(), data, null);
                    setReferences(data, false);
                    afterSetReferences(data, null, true);
                    mongoTemplate.save(data);
                });
                return;
            }
        }
    }
    
	public void validateUnique(String property, Object value, String message) {
		if (value != null && value != "") {
			Object valueQuery = value;
			if (value instanceof String && ObjectId.isValid((String)value)) {
				valueQuery = new ObjectId((String)value);
			}
			if (mongoTemplate.count(Query.query(Criteria.where(property).is(valueQuery)), entityClass) == 0) {
				return;
			}
		}
		throw new SimpleValidationException(new ValidationError(property, message));
	}
	
	public void validateUniqueId(T data) {
		validateUnique("id", data.getId(), "error.id.mustBeUnique");
	}
	
	protected List<T> filterPermittedItems(List<T> items) {
		List<T> result = new LinkedList<T>();
		if (items != null) {
			for (T data : items) {
				if (readManyItemFilter(data)) {
					result.add(data);
				}
			}
		}
		return result;
	}

	@Override
	public boolean readManyItemFilter(T item) {
		return isPermitted(PermissionAction.READ, item.getId());
	}
	
	public NotFoundException notFoundException(String id) {
	    return new NotFoundException(entityClass.getSimpleName(), id);
	}
}
