package se.leafcoders.rosette.service;

import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.leafcoders.rosette.model.resource.*;
import se.leafcoders.rosette.model.upload.UploadFolderRef;

@Service
public class ResourceTypeService extends MongoTemplateCRUD<ResourceType> {

	@Autowired
	private GroupService groupService;
	@Autowired
	private UploadFolderService uploadFolderService;

	public ResourceTypeService() {
		super("resourceTypes", ResourceType.class);
	}

	@Override
	public ResourceType create(ResourceType data, HttpServletResponse response) {
		validateUniqueId(data);
		return super.create(data, response);
	}

	@Override
	public void insertDependencies(ResourceType data) {
		// TODO: Use methodService here
		if (data instanceof UserResourceType) {
			UserResourceType resourceType = (UserResourceType) data;
			if (resourceType.getGroup() != null) {
				resourceType.setGroup(groupService.read(resourceType.getGroup().getId()));
			}
		} else if (data instanceof UploadResourceType) {
			UploadResourceType resourceType = (UploadResourceType) data;
			if (resourceType.getUploadFolder() != null) {
				resourceType.setUploadFolder(new UploadFolderRef(uploadFolderService.read(resourceType.getUploadFolder().getId())));
			}
		}
	}
}
