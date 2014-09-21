package se.ryttargardskyrkan.rosette.model;

import org.springframework.data.annotation.Transient;

public class ObjectReferenceOrText<T> {
    private String idRef;

    private String text;

    @Transient
    private T referredObject;

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

    public T getReferredObject() {
        return referredObject;
    }

    public void setReferredObject(T referredObject) {
        this.referredObject = referredObject;
    }
}
