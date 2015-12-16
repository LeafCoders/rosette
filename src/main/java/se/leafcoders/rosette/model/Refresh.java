package se.leafcoders.rosette.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "refreshs")
public class Refresh {

	@Id
    private String id;
	private String collectionClassName;

	// Getters and setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCollectionClassName() {
		return collectionClassName;
	}

	public void setCollectionClassName(String collectionClassName) {
		this.collectionClassName = collectionClassName;
	}
}
