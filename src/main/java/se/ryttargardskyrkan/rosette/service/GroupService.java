package se.ryttargardskyrkan.rosette.service;

import org.springframework.stereotype.Service;
import se.ryttargardskyrkan.rosette.model.Group;

@Service
public class GroupService extends MongoTemplateCRUD<Group> {

	public GroupService() {
		super("groups", Group.class);
	}

	@Override
	public void insertDependencies(Group data) {
	}
}
