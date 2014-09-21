package se.ryttargardskyrkan.rosette.model;

import org.springframework.data.annotation.Transient;

public class ObjectReference<T> {
    private String idRef;

    @Transient
    private T referredObject;

    // Constructors

    public ObjectReference() {}

    public ObjectReference(String id) {
    	idRef = id;
    }

    // Getters and setters

    public String getIdRef() {
        return idRef;
    }

    public void setIdRef(String idRef) {
        this.idRef = idRef;
    }

    public T getReferredObject() {
        return referredObject;
    }

    public void setReferredObject(T referredObject) {
        this.referredObject = referredObject;
    }
}
