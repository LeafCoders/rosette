package se.ryttargardskyrkan.rosette.service;

import java.util.ArrayList;
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
import se.ryttargardskyrkan.rosette.model.IdBasedModel;
import se.ryttargardskyrkan.rosette.model.User;
import se.ryttargardskyrkan.rosette.model.ValidationError;

abstract class MongoTemplateCRUD<T> implements StandardCRUD<T> {
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

	@Override
	public T create(T data, HttpServletResponse response) {
		security.checkPermission("create:" + permissionType);
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
		security.checkPermission("read:" + permissionType + ":" + id);
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
				if (security.isPermitted("read:" + permissionType + ":" + ((IdBasedModel)data).getId())) {
			        insertDependencies(data);
					result.add(data);
				}
			}
		}
		return result;
	}

	@Override
	public void update(String id, T data, Update update, HttpServletResponse response) {
		security.checkPermission("update:" + permissionType + ":" + id);
		security.validate(data);

		if (mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(id)), update, entityClass).getN() == 0) {
			throw new NotFoundException();
		}
		response.setStatus(HttpStatus.OK.value());
	}

	@Override
	public void delete(String id, HttpServletResponse response) {
		security.checkPermission("delete:" + permissionType + ":" + id);
		security.checkNotReferenced(id, permissionType);
        if (mongoTemplate.findAndRemove(Query.query(Criteria.where("id").is(id)), entityClass) == null) {
			throw new NotFoundException();
		}
		response.setStatus(HttpStatus.OK.value());
	}
	
	public void validateUnique(String property, Object value, String message) {
		if (mongoTemplate.count(Query.query(Criteria.where(property).is(value)), entityClass) > 0) {
			throw new SimpleValidationException(new ValidationError(property, message));
		}
	}
}
