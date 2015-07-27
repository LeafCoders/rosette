package se.leafcoders.rosette.model;

import com.fasterxml.jackson.databind.JsonNode;

public interface BaseModel {
	public String getId();
	public void setId(String id);

	public void update(JsonNode rawData, BaseModel updateFrom);
}
