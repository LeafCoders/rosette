package se.leafcoders.rosette.model;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.databind.JsonNode;

@Document(collection = "textValue")
public class TextValue extends IdBasedModel {

	@NotEmpty(message = "textValue.format.notEmpty")
	private String format;

    private Boolean isPublic;
	
	private String value;
	
	@Override
	public void update(JsonNode rawData, BaseModel updateFrom) {
		TextValue dataUpdate = (TextValue) updateFrom;
		if (rawData.has("format")) {
			setFormat(dataUpdate.getFormat());
		}
		if (rawData.has("isPublic")) {
			setIsPublic(dataUpdate.getIsPublic());
		}
		if (rawData.has("value")) {
			setValue(dataUpdate.getValue());
		}
	}

    // Getters and setters

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String data) {
        this.value = data;
    }
}
