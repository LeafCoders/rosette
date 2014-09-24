package se.ryttargardskyrkan.rosette.model;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "groups")
public class Group extends IdBasedModel {

	@Indexed(unique = true)
    @NotEmpty(message = "group.name.notEmpty")
	private String name;
	private String description;

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
}
