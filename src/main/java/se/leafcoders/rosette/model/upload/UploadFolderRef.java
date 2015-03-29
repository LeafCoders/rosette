package se.leafcoders.rosette.model.upload;

import javax.validation.constraints.Pattern;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.Id;

public class UploadFolderRef {
	@Id
	@Pattern(regexp = "[a-z][a-zA-Z0-9]+", message = "error.id.notValidFormat")
	private String id;

    @NotEmpty(message = "type.name.notEmpty")
	private String name;

	public UploadFolderRef() {}
	
	public UploadFolderRef(UploadFolder uploadFolder) {
		id = uploadFolder.getId();
		name = uploadFolder.getName();
	}

    // Getter and setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
