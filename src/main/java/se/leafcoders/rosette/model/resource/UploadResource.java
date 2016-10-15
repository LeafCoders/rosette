package se.leafcoders.rosette.model.resource;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import se.leafcoders.rosette.model.reference.UploadFileRefs;
import se.leafcoders.rosette.model.upload.UploadFile;
import se.leafcoders.rosette.validator.CheckReference;

public class UploadResource extends Resource {
    @NotNull(message = "uploadResource.uploads.notNull")
    @Valid
    @CheckReference(model = UploadFile.class)
    private UploadFileRefs uploads;

    // Constructors

    public UploadResource() {
		super("upload");
    }

    // Getters and setters

	public UploadFileRefs getUploads() {
		return uploads;
	}

	public void setUploads(UploadFileRefs uploads) {
		this.uploads = uploads;
	}
}
