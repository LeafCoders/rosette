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
import se.leafcoders.rosette.security.PermissionType;
import se.leafcoders.rosette.util.ManyQuery;

@Service
public class UserService extends MongoTemplateCRUD<User> {

    @Autowired
    private SecurityService securityService;

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

	public UserRef readAsRef(String id, boolean checkPermissions) {
        return new UserRef(super.read(id, checkPermissions));
	}

	@Override
	public void beforeUpdate(String id, User updateData, User dataInDatabase) {
		if (updateData != null && updateData.getPassword() != null) {
	        security.resetPermissionCache();
		}
	}	
	
	@Override
	public List<User> readMany(final ManyQuery manyQuery) {
		List<User> users = super.readMany(manyQuery);
		for (User user : users) {
	        user.setHashedPassword(null);
		}
		return users;
	}

    @Override
    public void delete(String id, HttpServletResponse response) {
        super.delete(id, response);
        securityService.resetPermissionCache();
    }

	@Override
	public void setReferences(User data, boolean checkPermissions) {
	}

    @Override
    public Class<?>[] references() {
        return new Class<?>[] { };
    }
}
