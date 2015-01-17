package se.ryttargardskyrkan.rosette.service;

import java.util.LinkedList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import se.ryttargardskyrkan.rosette.exception.NotFoundException;
import se.ryttargardskyrkan.rosette.exception.SimpleValidationException;
import se.ryttargardskyrkan.rosette.model.BaseModel;
import se.ryttargardskyrkan.rosette.model.ValidationError;

abstract class MongoTemplateCRUD<T extends BaseModel> implements StandardCRUD<T> {
	@Autowired
	protected MongoTemplate mongoTemplate;
	@Autowired
	protected SecurityService security;
	private final String permissionType;
	private final Class<T> entityClass;

	public MongoTemplateCRUD(String permissionType, Class<T> entityClass) {
		this.permissionType = permissionType;
		this.entityClass = entityClass;
	}

	protected void checkPermission(String accessType) {
		security.checkPermission(accessType + ":" + permissionType);
	}

	protected void checkPermission(String accessType, String id) {
		security.checkPermission(accessType + ":" + permissionType + ":" + id);
	}

	protected Query getIdQuery(String id) {
		return Query.query(Criteria.where("id").is(id));
	}

	@Override
	public T create(T data, HttpServletResponse response) {
		checkPermission("create");
		security.validate(data);
		mongoTemplate.insert(data);
		response.setStatus(HttpStatus.CREATED.value());
		return data;
	}

	@Override
	public T read(String id) {
        T data = readNoDep(id);
        insertDependencies(data);
		return data;
	}

	public T readNoDep(String id) {
		checkPermission("read", id);
        T data = mongoTemplate.findById(id, entityClass);
		if (data == null) {
			throw new NotFoundException();
		}
		return data;
	}
	
	@Override
	public List<T> readMany(final Query query) {
		List<T> dataInDatabase = mongoTemplate.find(query, entityClass);
		List<T> result = new LinkedList<T>();
		if (dataInDatabase != null) {
			for (T data : dataInDatabase) {
				if (security.isPermitted("read:" + permissionType + ":" + data.getId())) {
			        insertDependencies(data);
					result.add(data);
				}
			}
		}
		return result;
	}

	@Override
	public void update(String id, T data, Update update, HttpServletResponse response) {
		checkPermission("update", id);
		security.validate(data);

		if (mongoTemplate.updateFirst(getIdQuery(id), update, entityClass).getN() == 0) {
			throw new NotFoundException();
		}
		response.setStatus(HttpStatus.OK.value());
	}

	@Override
	public void delete(String id, HttpServletResponse response) {
		checkPermission("delete", id);
		security.checkNotReferenced(id, permissionType);
        if (mongoTemplate.findAndRemove(getIdQuery(id), entityClass) == null) {
			throw new NotFoundException();
		}
		response.setStatus(HttpStatus.OK.value());
	}
	
	public void validateUnique(String property, Object value, String message) {
		if (value == null || value == "" || mongoTemplate.count(Query.query(Criteria.where(property).is(value)), entityClass) > 0) {
			throw new SimpleValidationException(new ValidationError(property, message));
		}
	}
	
	public void validateUniqueId(T data) {
		validateUnique("id", data.getId(), "error.id.mustBeUnique");
	}
}
