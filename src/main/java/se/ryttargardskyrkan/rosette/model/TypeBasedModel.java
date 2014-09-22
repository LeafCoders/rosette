package se.ryttargardskyrkan.rosette.model;

import javax.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.mongodb.core.index.Indexed;

public class TypeBasedModel extends IdBasedModel {
	@Indexed(unique = true)
	@Pattern(regexp = "[a-z][a-zA-Z]+", message = "type.key.notValidFormat")
	protected String key;

    @NotEmpty(message = "type.name.notEmpty")
    protected String name;

    @Length(max = 200, message = "type.description.max200Chars")
    protected String description;

    // Getter and setters

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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
