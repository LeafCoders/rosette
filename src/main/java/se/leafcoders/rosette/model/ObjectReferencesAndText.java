package se.ryttargardskyrkan.rosette.model;

import java.util.List;

public class ObjectReferencesAndText<T> {
    private List<T> refs;

    private String text;

    public boolean hasRefs() {
        return (refs != null) && !refs.isEmpty();
    }

    public boolean hasText() {
        return (text != null) && !text.isEmpty();
    }
    
    public int totalNumRefsAndText() {
    	return (hasRefs() ? getRefs().size() : 0) + (hasText() ? 1 : 0);
    }

    // Getters and setters

	public List<T> getRefs() {
		return refs;
	}

	public void setRefs(List<T> refs) {
		this.refs = refs;
	}

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
