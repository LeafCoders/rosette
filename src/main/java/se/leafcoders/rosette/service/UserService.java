package se.leafcoders.rosette.service;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import se.leafcoders.rosette.exception.SimpleValidationException;
import se.leafcoders.rosette.model.User;
import se.leafcoders.rosette.model.error.ValidationError;
import se.leafcoders.rosette.model.reference.UserRef;
import se.leafcoders.rosette.security.MongoRealm;
import se.leafcoders.rosette.security.PermissionType;

@Service
public class UserService extends MongoTemplateCRUD<User> {

	@Autowired
	private MongoRealm mongoRealm;
	
	public UserService() {
		super(User.class, PermissionType.USERS);
	}

	@Override
	public User create(User user, HttpServletResponse response) {
		long count = mongoTemplate.count(Query.query(Criteria.where("email").is(user.getEmail())), User.class);
		if (count > 0) {
			throw new SimpleValidationException(new ValidationError("email", "user.email.mustBeUnique"));
		} else {
			return super.create(user, response);
		}
	}

	@Override
	public User read(String id) {
        User user = super.read(id);
        user.setHashedPassword(null);
		return user;
	}

	public UserRef readAsRef(String id) {
        return new UserRef(super.read(id));
	}

	@Override
	public void beforeUpdate(String id, User updateData) {
		if (updateData.getPassword() != null) {
			mongoRealm.clearCache(null);
		}
	}	
	
	@Override
	public List<User> readMany(final Query query) {
		List<User> users = super.readMany(query);
		for (User user : users) {
	        user.setHashedPassword(null);
		}
		return users;
	}

	@Override
	public void insertDependencies(User data) {
	}
}
