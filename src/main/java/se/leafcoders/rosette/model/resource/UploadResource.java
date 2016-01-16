package se.leafcoders.rosette.model.resource;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import se.leafcoders.rosette.model.reference.UploadResponseRefs;
import se.leafcoders.rosette.model.upload.UploadResponse;
import se.leafcoders.rosette.validator.CheckReference;

public class UploadResource extends Resource {
    @NotNull(message = "uploadResource.uploads.notNull")
    @Valid
    @CheckReference(model = UploadResponse.class)
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
