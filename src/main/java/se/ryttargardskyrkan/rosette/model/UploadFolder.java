package se.ryttargardskyrkan.rosette.model;

import java.util.List;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.mongodb.core.mapping.Document;
import javax.validation.constraints.Pattern;

@Document(collection = "uploadFolders")
public class UploadFolder extends IdBasedModel {

	@NotEmpty(message = "uploadFolder.title.notEmpty")
	private String title;

	// Name will be used as part of the upload url, so it must only contain valid url chars
	@NotEmpty(message = "uploadFolder.name.notEmpty")
	@Pattern(regexp="[a-z]")
	private String name;

	@NotEmpty(message = "uploadFolder.mimeTypes.notEmpty")
	private List<String> mimeTypes;

	// Getters and setters

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getMimeTypes() {
		return mimeTypes;
	}

	public void setMimeTypes(List<String> mimeTypes) {
		this.mimeTypes = mimeTypes;
	}
}
