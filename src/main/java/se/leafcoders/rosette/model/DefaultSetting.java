package se.leafcoders.rosette.model;

import javax.validation.constraints.NotNull;

public class DefaultSetting<T> {
	
	@NotNull(message = "defaultSetting.value.notNull")
	private T value;
	private Boolean allowChange = false;

	// Getters and setters
	
	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public Boolean getAllowChange() {
		return allowChange;
	}

	public void setAllowChange(Boolean allowChange) {
		this.allowChange = allowChange;
	}
}
