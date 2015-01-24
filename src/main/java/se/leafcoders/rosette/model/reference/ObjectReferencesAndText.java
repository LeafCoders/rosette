package se.leafcoders.rosette.model.reference;

import se.leafcoders.rosette.model.BaseModel;

public class ObjectReferencesAndText<T extends BaseModel> {
    private ObjectReferences<T> refs;

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

    public boolean updateRef(T ref) {
    	return refs.updateRef(ref);
    }

    // Getters and setters

	public ObjectReferences<T> getRefs() {
		return refs;
	}

	public void setRefs(ObjectReferences<T> refs) {
		this.refs = refs;
	}

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
