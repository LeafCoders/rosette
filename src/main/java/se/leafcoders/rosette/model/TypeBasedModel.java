package se.leafcoders.rosette.model;

import javax.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.Id;

public abstract class TypeBasedModel implements BaseModel {
	@Id
	@Pattern(regexp = "[a-z][a-zA-Z0-9]+", message = "error.id.notValidFormat")
	protected String id;

    @NotEmpty(message = "type.name.notEmpty")
    protected String name;

    @Length(max = 200, message = "type.description.max200Chars")
    protected String description;

    @Override
	public void update(BaseModel updateFrom) {
    	TypeBasedModel modelUpdate = (TypeBasedModel) updateFrom;
    	if (modelUpdate.getName() != null) {
    		setName(modelUpdate.getName());
    	}
    	if (modelUpdate.getDescription() != null) {
    		setDescription(modelUpdate.getDescription());
    	}
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
