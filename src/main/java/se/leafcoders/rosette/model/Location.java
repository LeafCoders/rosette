package se.leafcoders.rosette.model;

import javax.validation.constraints.NotNull;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.databind.JsonNode;
import se.leafcoders.rosette.model.upload.UploadFile;
import se.leafcoders.rosette.validator.CheckReference;

@Document(collection = "locations")
public class Location extends IdBasedModel {

    @NotNull(message = "location.name.notNull")
	@Indexed
	private String name;
	private String description;

	// Image that shows the direction to the location
	@CheckReference
	private UploadFile directionImage;
	
	@Override
	public void update(JsonNode rawData, BaseModel updateFrom) {
		Location locationUpdate = (Location) updateFrom;
    	if (rawData.has("name")) {
    		setName(locationUpdate.getName());
    	}
    	if (rawData.has("description")) {
    		setDescription(locationUpdate.getDescription());
    	}
    	if (rawData.has("directionImage")) {
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

    public UploadFile getDirectionImage() {
        return directionImage;
    }

    public void setDirectionImage(UploadFile directionImage) {
        this.directionImage = directionImage;
    }
}
