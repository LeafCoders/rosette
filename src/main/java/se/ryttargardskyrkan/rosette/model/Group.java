package se.ryttargardskyrkan.rosette.model;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import javax.validation.constraints.NotNull;

@Document(collection = "groups")
public class Group extends IdBasedModel {

    @NotNull(message = "group.name.notNull")
	@Indexed(unique = true)
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
