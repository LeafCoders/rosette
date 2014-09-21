package se.ryttargardskyrkan.rosette.model;

import java.util.List;

public class ObjectReferencesAndText<T> {
    private List<ObjectReference<T>> refs;

    private String text;

    public boolean hasRefs() {
        return (refs != null) && !refs.isEmpty();
    }

    public boolean hasText() {
        return (text != null) && !text.isEmpty();
    }

    // Getters and setters

	public List<ObjectReference<T>> getRefs() {
		return refs;
	}

	public void setRefs(List<ObjectReference<T>> refs) {
		this.refs = refs;
	}

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
