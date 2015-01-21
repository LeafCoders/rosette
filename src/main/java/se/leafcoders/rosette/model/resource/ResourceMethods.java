package se.leafcoders.rosette.model.resource;

import org.springframework.data.mongodb.core.query.Update;

public interface ResourceMethods {
	
	Update createAssignUpdate(ResourceType resourceType);

	void insertDependencies();
}
