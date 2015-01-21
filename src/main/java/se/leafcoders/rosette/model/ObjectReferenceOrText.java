package se.ryttargardskyrkan.rosette.model;

public class ObjectReferenceOrText<T extends BaseModel> {
    private T ref;

    private String text;

    public boolean hasRef() {
        return (ref != null) && !ref.getId().isEmpty();
    }

    public boolean hasText() {
        return (text != null) && !text.isEmpty();
    }

    public String refId() {
    	return hasRef() ? ref.getId() : null;
    }
    
    // Getters and setters

    public T getRef() {
        return ref;
    }

    public void setRef(T ref) {
        this.ref = ref;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
