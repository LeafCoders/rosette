package se.ryttargardskyrkan.rosette.model.resource;

import java.util.List;
import javax.validation.constraints.NotNull;
import se.ryttargardskyrkan.rosette.model.UploadResponse;

public class UploadResource extends Resource {
	@NotNull(message = "uploadResource.uploads.notNull")
    private List<UploadResponse> uploads;

    // Constructors

    public UploadResource() {
		super("upload");
    }

    // Getters and setters

	public List<UploadResponse> getUploads() {
		return uploads;
	}

	public void setUploads(List<UploadResponse> uploads) {
		this.uploads = uploads;
	}
}
