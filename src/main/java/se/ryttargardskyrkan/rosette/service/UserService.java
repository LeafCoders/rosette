package se.ryttargardskyrkan.rosette.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import se.ryttargardskyrkan.rosette.model.User;

@Service
public class UserService {
	private MongoTemplate mongoTemplate;
	
	@Autowired
	public UserService(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}
	
	public User findUserById(String id) {
		return this.mongoTemplate.findById(id, User.class);
	}

	public User findUserByUsername(String username) {
		return this.mongoTemplate.findOne(Query.query(Criteria.where("username").is(username)), User.class);
	}
}
