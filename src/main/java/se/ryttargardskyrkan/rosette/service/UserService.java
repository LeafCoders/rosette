package se.ryttargardskyrkan.rosette.service;

import javax.servlet.http.HttpServletResponse;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import se.ryttargardskyrkan.rosette.exception.SimpleValidationException;
import se.ryttargardskyrkan.rosette.model.User;
import se.ryttargardskyrkan.rosette.model.ValidationError;

@Service
public class UserService extends MongoTemplateCRUD<User> {

	public UserService() {
		super("users", User.class);
	}

	@Override
	public User create(User user, HttpServletResponse response) {
		long count = mongoTemplate.count(Query.query(Criteria.where("username").is(user.getUsername())), User.class);
		if (count > 0) {
			throw new SimpleValidationException(new ValidationError("username", "user.username.duplicatedUsernameNotAllowed"));
		} else {
			return super.create(user, response);
		}
	}
	
	@Override
	public void insertDependencies(User data) {
	}
}
