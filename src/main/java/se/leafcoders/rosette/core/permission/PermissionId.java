package se.leafcoders.rosette.core.permission;

import se.leafcoders.rosette.core.persistable.Persistable;

public class PermissionId<T extends Persistable> {

    private Long id;
    private T item;

    public PermissionId() {
        this(null, null);
    }

    public PermissionId(Long id) {
        this(null, id);
    }

    public PermissionId(T item) {
        this(item, null);
    }

    public PermissionId(T item, Long id) {
        this.item = item;
        this.id = item != null ? item.getId() : id;
    }

    public Long getId() {
        return id;
    }

    public T getItem() {
        return item;
    }
}
