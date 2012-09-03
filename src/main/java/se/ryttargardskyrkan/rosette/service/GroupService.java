package se.ryttargardskyrkan.rosette.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import se.ryttargardskyrkan.rosette.model.Group;

@Service
public class GroupService {

	private MongoTemplate mongoTemplate;

	@Autowired
	public GroupService(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	public Group findGroupById(String id) {
		return this.mongoTemplate.findById(id, Group.class);
	}
	
	public Group findGroupByName(String name) {
		return this.mongoTemplate.findOne(Query.query(Criteria.where("name").is(name)), Group.class);
	}
}
