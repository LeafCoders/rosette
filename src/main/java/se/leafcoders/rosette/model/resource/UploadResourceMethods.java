package se.leafcoders.rosette.model.resource;

import org.springframework.data.mongodb.core.query.Update;
import se.leafcoders.rosette.exception.SimpleValidationException;
import se.leafcoders.rosette.model.error.ValidationError;
import se.leafcoders.rosette.model.upload.UploadFolderRef;
import se.leafcoders.rosette.model.upload.UploadResponse;
import se.leafcoders.rosette.service.UploadService;

public class UploadResourceMethods implements ResourceMethods {
	protected final UploadResource resource;
	protected UploadService uploadService;

	public UploadResourceMethods(UploadResource resource, UploadService uploadService) {
		this.resource = resource;
		this.uploadService = uploadService;
	}

	public Update createAssignUpdate(ResourceType resourceTypeIn, boolean checkPermissions) {
		validateAndUpdate(resourceTypeIn, checkPermissions);
		return new Update().set("resources.$.uploads", resource.getUploads());
	}

	public void setReferences(boolean checkPermissions) {
		validateAndUpdate(resource.getResourceType(), checkPermissions);
	}

	private void validateAndUpdate(ResourceType resourceType, boolean checkPermissions) {
		if (!(resource.getResourceType() instanceof UploadResourceType)) {
			throw new SimpleValidationException(new ValidationError("resource", "uploadResource.wrongResourceType"));
		}

		if (resource.getUploads() != null) {
			UploadResourceType uploadResourceType = (UploadResourceType) resource.getResourceType();
			
			if (uploadResourceType.getMultiSelect() != Boolean.TRUE && resource.getUploads().size() > 1) {
				throw new SimpleValidationException(new ValidationError("resource", "uploadResource.multiUploadsNotAllowed"));
			}
			
			UploadFolderRef uploadFolder = uploadResourceType.getUploadFolder();
			if (resource.getUploads().hasRefs() && !uploadService.containsUploads(uploadFolder.getId(), resource.getUploads())) {
				throw new SimpleValidationException(new ValidationError("resource", "uploadResource.uploadDoesNotExistInFolder"));
			}
			
			for (UploadResponse upload : resource.getUploads()) {
				resource.getUploads().updateRef(uploadService.read(upload.getId(), checkPermissions));
			}
		}
	}
}
