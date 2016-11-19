package se.leafcoders.rosette.model.resource;

import org.springframework.data.mongodb.core.query.Update;

public interface ResourceMethods {
	
	Update updateQuery(ResourceType resourceType, boolean checkPermissions);

	void setReferences(boolean checkPermissions);
}
