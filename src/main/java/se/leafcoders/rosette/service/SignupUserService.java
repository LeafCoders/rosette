package se.leafcoders.rosette.service;

import javax.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import se.leafcoders.rosette.model.SignupUser;

@Service
public class SignupUserService extends MongoTemplateCRUD<SignupUser> {

	public SignupUserService() {
		super("signupUsers", SignupUser.class);
	}

	@Override
	public SignupUser create(SignupUser data, HttpServletResponse response) {
		security.validate(data);
		mongoTemplate.insert(data);
		response.setStatus(HttpStatus.CREATED.value());
		return data;
	}

	@Override
	public void insertDependencies(SignupUser data) {
	}
	
	public SignupUser getLatestSignupUser() {
		Query queryLastCreated = new Query().with(new Sort(new Sort.Order(Sort.Direction.DESC, "createdTime")));
		return mongoTemplate.findOne(queryLastCreated, SignupUser.class);
	}
}
