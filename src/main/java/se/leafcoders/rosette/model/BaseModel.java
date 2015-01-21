package se.leafcoders.rosette.model;

public interface BaseModel {
	public String getId();
	public void setId(String id);

	public void update(BaseModel updateFrom);
}
