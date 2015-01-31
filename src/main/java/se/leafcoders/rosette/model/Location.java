package se.leafcoders.rosette.model;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import javax.validation.constraints.NotNull;

@Document(collection = "locations")
public class Location extends IdBasedModel {

    @NotNull(message = "location.name.notNull")
	@Indexed
	private String name;
	private String description;

	// Image that shows the direction to the location
	private UploadResponse directionImage;
	
	@Override
	public void update(BaseModel updateFrom) {
		Location locationUpdate = (Location) updateFrom;
    	if (locationUpdate.getName() != null) {
    		setName(locationUpdate.getName());
    	}
    	if (locationUpdate.getDescription() != null) {
    		setDescription(locationUpdate.getDescription());
    	}
    	if (locationUpdate.getDirectionImage() != null) {
    		setDirectionImage(locationUpdate.getDirectionImage());
    	}
	}

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

    public UploadResponse getDirectionImage() {
        return directionImage;
    }

    public void setDirectionImage(UploadResponse directionImage) {
        this.directionImage = directionImage;
    }
}
