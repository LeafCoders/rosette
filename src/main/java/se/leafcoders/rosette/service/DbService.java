package se.leafcoders.rosette.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import se.leafcoders.rosette.model.BaseModel;
import se.leafcoders.rosette.util.QueryId;

public class DbService<T extends BaseModel> {

    @Autowired
    protected MongoTemplate mongoTemplate;

    private final Class<T> entityClass;
    
    public DbService(Class<T> clazz) {
        entityClass = clazz;
    }

    public void create(T data) {
        mongoTemplate.insert(data);
    }

    public T deleteById(String id) {
        Query query = Query.query(Criteria.where("id").is(QueryId.get(id)));
        return mongoTemplate.findAndRemove(query, entityClass);
    }

	public T findById(String id) {
	    Query query = Query.query(Criteria.where("id").is(QueryId.get(id)));
	    return mongoTemplate.findOne(query, entityClass);
	}
    
    public T findBy(String key, Object value) {
        Query query = Query.query(Criteria.where(key).is(value));
        return mongoTemplate.findOne(query, entityClass);
    }
}
