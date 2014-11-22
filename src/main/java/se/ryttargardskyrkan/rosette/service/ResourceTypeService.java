package se.ryttargardskyrkan.rosette.service;

import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.ryttargardskyrkan.rosette.model.Group;
import se.ryttargardskyrkan.rosette.model.ObjectReference;
import se.ryttargardskyrkan.rosette.model.resource.*;
import se.ryttargardskyrkan.rosette.security.MongoRealm;

@Service
public class ResourceTypeService extends MongoTemplateCRUD<ResourceType> {

	@Autowired
	private MongoRealm mongoRealm;
	@Autowired
	private GroupService groupService;

	public ResourceTypeService() {
		super("resourceTypes", ResourceType.class);
	}

	@Override
	public ResourceType create(ResourceType data, HttpServletResponse response) {
		validateUnique("key", data.getKey(), "resourceType.key.mustBeUnique");
		return super.create(data, response);
	}

	@Override
	public void insertDependencies(ResourceType data) {
		if (data instanceof UserResourceType) {
			ObjectReference<Group> group = ((UserResourceType) data).getGroup();
			if (group != null) {
				group.setReferredObject(groupService.readNoDep(group.getIdRef()));
			}
		}
	}
}
