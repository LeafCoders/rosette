package se.ryttargardskyrkan.rosette.model;

import org.springframework.data.annotation.Id;

public class IdBasedModel {
	@Id
	protected String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
