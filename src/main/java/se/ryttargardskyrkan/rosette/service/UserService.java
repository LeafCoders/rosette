package se.ryttargardskyrkan.rosette.service;

import org.springframework.stereotype.Service;
import se.ryttargardskyrkan.rosette.model.User;

@Service
public class UserService extends MongoTemplateCRUD<User> {

	public UserService() {
		super("users", User.class);
	}
	
	@Override
	public void insertDependencies(User data) {
	}
}
