package se.ryttargardskyrkan.rosette.model.resource;

import java.util.List;
import org.springframework.data.mongodb.core.query.Update;
import se.ryttargardskyrkan.rosette.exception.SimpleValidationException;
import se.ryttargardskyrkan.rosette.model.ObjectReference;
import se.ryttargardskyrkan.rosette.model.UploadResponse;
import se.ryttargardskyrkan.rosette.model.ValidationError;
import se.ryttargardskyrkan.rosette.service.UploadService;

public class UploadResourceMethods implements ResourceMethods {
	protected final UploadResource resource;
	UploadService uploadService;

	public UploadResourceMethods(UploadResource resource, UploadService uploadService) {
		this.resource = resource;
		this.uploadService = uploadService;
	}

	public Update createAssignUpdate(ResourceType resourceType) {
		if (resourceType instanceof UploadResourceType) {
			UploadResourceType uploadResourceType = (UploadResourceType) resourceType;
			
			if (!uploadResourceType.getMultiSelect() && resource.getUploads().size() > 1) {
				throw new SimpleValidationException(new ValidationError("resource", "uploadResource.multiUploadsNotAllowed"));
			}
			
			String folder = uploadResourceType.getFolderName();
			if (uploadService.containsUploads(folder, resource.getUploads())) {
				return new Update().set("resources.$.uploads", resource.getUploads());
			}
		}
		throw new SimpleValidationException(new ValidationError("resource", "uploadResource.assignedUploadNotInFolder"));
	}

	public void insertDependencies() {
		final List<ObjectReference<UploadResponse>> uploadRefs = resource.getUploads();
		if (uploadRefs != null) {
			for (ObjectReference<UploadResponse> uploadRef : uploadRefs) {
				uploadRef.setReferredObject(uploadService.read(uploadRef.getIdRef()));
			}
		}
	}
}
