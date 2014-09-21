package se.ryttargardskyrkan.rosette.model;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import javax.validation.constraints.NotNull;

@Document(collection = "locations")
public class Location extends IdBasedModel {

    @NotNull(message = "location.name.notNull")
	@Indexed(unique = true)
	private String name;
	private String description;

	// Image that shows the direction to the location
	private ObjectReference<UploadResponse> directionImage;
	
	// Getters and setters

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

    public ObjectReference<UploadResponse> getDirectionImage() {
        return directionImage;
    }

    public void setDirectionImage(ObjectReference<UploadResponse> directionImage) {
        this.directionImage = directionImage;
    }
}
