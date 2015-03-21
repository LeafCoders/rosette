package se.leafcoders.rosette.service;

import java.util.LinkedList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import se.leafcoders.rosette.exception.NotFoundException;
import se.leafcoders.rosette.exception.SimpleValidationException;
import se.leafcoders.rosette.model.BaseModel;
import se.leafcoders.rosette.model.ValidationError;

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
		insertDependencies(data);
		security.validate(data);
		mongoTemplate.insert(data);
		response.setStatus(HttpStatus.CREATED.value());
		return data;
	}

	@Override
	public T read(String id) {
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
					result.add(data);
				}
			}
		}
		return result;
	}

	@Override
	public void update(String id, T updateData, HttpServletResponse response) {
		checkPermission("update", id);
		T dataInDbToUpdate = read(id);
		if (dataInDbToUpdate == null) {
			throw new NotFoundException();
		}
		insertDependencies(updateData);
		dataInDbToUpdate.update(updateData);
		security.validate(dataInDbToUpdate);
		mongoTemplate.save(dataInDbToUpdate);
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
