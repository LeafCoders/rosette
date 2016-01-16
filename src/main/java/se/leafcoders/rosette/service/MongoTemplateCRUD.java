package se.leafcoders.rosette.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import se.leafcoders.rosette.exception.NotFoundException;
import se.leafcoders.rosette.exception.SimpleValidationException;
import se.leafcoders.rosette.model.BaseModel;
import se.leafcoders.rosette.model.error.ValidationError;
import se.leafcoders.rosette.security.PermissionAction;
import se.leafcoders.rosette.security.PermissionCheckFilter;
import se.leafcoders.rosette.security.PermissionType;
import se.leafcoders.rosette.security.PermissionValue;
import se.leafcoders.rosette.util.ManyQuery;
import se.leafcoders.rosette.util.QueryId;

abstract class MongoTemplateCRUD<T extends BaseModel> implements StandardCRUD<T> {
    static final Logger logger = LoggerFactory.getLogger(MongoTemplateCRUD.class);

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

	protected void checkPermission(PermissionAction actionType, T data) {
        security.checkPermission(new PermissionValue(permissionType, actionType, data.getId()));
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
		checkPermission(PermissionAction.CREATE, data);
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
        T data = mongoTemplate.findById(id, entityClass);
        if (data == null) {
            throw notFoundException(id);
        }
        if (checkPermissions) {
            checkPermission(PermissionAction.READ, data);
        }
        return data;
	}

    @Override
    public List<T> readMany(final ManyQuery manyQuery) {
        return this.readMany(manyQuery, true);
    }

    @Override
    public List<T> readMany(final ManyQuery manyQuery, boolean checkPermissions) {
        List<T> items = mongoTemplate.find(manyQuery.getQuery(), entityClass);
        if (checkPermissions) {
            return filterPermittedItems(items, manyQuery);
        } else {
            return manyQuery.filter(items);
        }
    }

    @Override
	public void update(String id, HttpServletRequest request, HttpServletResponse response) {
        T dataInDbToUpdate = read(id);
        if (dataInDbToUpdate == null) {
            throw notFoundException(id);
        }
		checkPermission(PermissionAction.UPDATE, dataInDbToUpdate);

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

	/**
	 * @param id Id of object to update
	 * @param updateData Data to update existing object with. Will be null when refreshing database
	 * @param dataInDbToUpdate Existing object from database
	 */
	protected void beforeUpdate(String id, T updateData, T dataInDbToUpdate) {
	}
	
    protected void afterSetReferences(T updateData, T dataInDbToUpdate, boolean checkPermissions) {
    }

	@Override
	public void delete(String id, HttpServletResponse response) {
		checkPermission(PermissionAction.DELETE, read(id, false));
		security.checkNotReferenced(id, entityClass);
        if (mongoTemplate.findAndRemove(getIdQuery(id), entityClass) == null) {
			throw notFoundException(id);
		}
		response.setStatus(HttpStatus.OK.value());
	}
	
    @Override
    public void refresh(Set<String> changedCollections) {
        for (Class<?> refClass : references()) {
            if (changedCollections.contains(refClass.getSimpleName())) {
                List<T> items = readMany(new ManyQuery(), false);
                items.forEach((T data) -> {
                    try {
                        beforeUpdate(data.getId(), null, data);
                        setReferences(data, false);
                        afterSetReferences(data, data, false);
                        mongoTemplate.save(data);
                    } catch (Exception exception) {
                        logger.warn("Failed to refresh (" + entityClass.getSimpleName() + ") with id (" + data.getId() + "). Exception: " + exception.getMessage());
                    }
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
	
	protected List<T> filterPermittedItems(List<T> items, ManyQuery manyQuery) {
		List<T> result = new LinkedList<T>();
		if (items != null) {
		    int skippedItems = 0;
			for (T data : items) {
				if (readManyItemFilter(data)) {
				    if (skippedItems >= manyQuery.getStartIndex()) {
				        result.add(data);
				    } else {
				        skippedItems++;
				    }
				}
				if (result.size() >= manyQuery.getMaxItems()) {
				    return result;
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
