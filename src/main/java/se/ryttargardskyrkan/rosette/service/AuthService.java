package se.ryttargardskyrkan.rosette.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
	
	private MongoTemplate mongoTemplate;

	@Autowired
	public AuthService(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}
	
	public List<String> getPermissions(String username) {
		List<String> permissions = new ArrayList<String>();
		
		
		
		return permissions;
	}
	
}
