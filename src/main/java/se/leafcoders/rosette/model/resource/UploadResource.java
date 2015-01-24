package se.leafcoders.rosette.model.resource;

import javax.validation.constraints.NotNull;
import se.leafcoders.rosette.model.reference.UploadResponseRefs;

public class UploadResource extends Resource {
	@NotNull(message = "uploadResource.uploads.notNull")
    private UploadResponseRefs uploads;

    // Constructors

    public UploadResource() {
		super("upload");
    }

    // Getters and setters

	public UploadResponseRefs getUploads() {
		return uploads;
	}

	public void setUploads(UploadResponseRefs uploads) {
		this.uploads = uploads;
	}
}
