package se.ryttargardskyrkan.rosette.model.resource;

import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;
import se.ryttargardskyrkan.rosette.model.ObjectReference;
import se.ryttargardskyrkan.rosette.model.UploadResponse;

public class UploadResource extends Resource {
	@NotNull(message = "uploadResource.uploads.notNull")
    private List<ObjectReference<UploadResponse>> uploads;

    // Constructors

    public UploadResource() {}

	public UploadResource(UploadResourceType uploadResourceType) {
		super("upload", uploadResourceType);
		setUploads(new ArrayList<ObjectReference<UploadResponse>>());
	}
	
    // Getters and setters

	public List<ObjectReference<UploadResponse>> getUploads() {
		return uploads;
	}

	public void setUploads(List<ObjectReference<UploadResponse>> uploads) {
		this.uploads = uploads;
	}
}
