package se.ryttargardskyrkan.rosette.model;

public class IdOrText {
    private String idRef;
    private String text;

    public boolean hasIdRef() {
    	return (idRef != null) && !idRef.isEmpty();
    }
    
    public boolean hasText() {
    	return (text != null) && !text.isEmpty();
    }
    
    // Getters and setters

    public String getIdRef() {
        return idRef;
    }

    public void setIdRef(String idRef) {
        this.idRef = idRef;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
