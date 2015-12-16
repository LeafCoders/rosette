package se.leafcoders.rosette.service;

import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.leafcoders.rosette.model.Group;
import se.leafcoders.rosette.model.resource.ResourceType;
import se.leafcoders.rosette.model.resource.UploadResourceType;
import se.leafcoders.rosette.model.resource.UserResourceType;
import se.leafcoders.rosette.model.upload.UploadFolder;
import se.leafcoders.rosette.model.upload.UploadFolderRef;
import se.leafcoders.rosette.security.PermissionType;

@Service
public class ResourceTypeService extends MongoTemplateCRUD<ResourceType> {

	@Autowired
	private GroupService groupService;
	@Autowired
	private UploadFolderService uploadFolderService;

	public ResourceTypeService() {
		super(ResourceType.class, PermissionType.RESOURCE_TYPES);
	}

	@Override
	public ResourceType create(ResourceType data, HttpServletResponse response) {
		validateUniqueId(data);
		return super.create(data, response);
	}

	public UserResourceType readUserResourceType(String resourceTypeId, boolean checkPermissions) {
	    ResourceType resourceType = super.read(resourceTypeId, checkPermissions);
	    if (resourceType != null && resourceType.getType().equals("user")) {
	        return (UserResourceType) resourceType;
	    }
	    throw notFoundException(resourceTypeId);
	}

    public UploadResourceType readUploadResourceType(String resourceTypeId) {
        ResourceType resourceType = super.read(resourceTypeId);
        if (resourceType != null && resourceType.getType().equals("upload")) {
            return (UploadResourceType) resourceType;
        }
        throw notFoundException(resourceTypeId);
    }

	@Override
	public void setReferences(ResourceType data, boolean checkPermissions) {
		// TODO: Use methodService here
		if (data instanceof UserResourceType) {
			UserResourceType resourceType = (UserResourceType) data;
			if (resourceType.getGroup() != null) {
				resourceType.setGroup(groupService.read(resourceType.getGroup().getId(), checkPermissions));
			}
		} else if (data instanceof UploadResourceType) {
			UploadResourceType resourceType = (UploadResourceType) data;
			if (resourceType.getUploadFolder() != null) {
				resourceType.setUploadFolder(new UploadFolderRef(uploadFolderService.read(resourceType.getUploadFolder().getId(), checkPermissions)));
			}
		}
	}

    @Override
    public Class<?>[] references() {
        return new Class<?>[] { Group.class, UploadFolder.class };
    }
}
