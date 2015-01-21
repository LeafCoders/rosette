package se.ryttargardskyrkan.rosette.model;

public interface BaseModel {
	public String getId();
	public void setId(String id);

	public void update(BaseModel updateFrom);
}
