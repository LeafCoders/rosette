package se.leafcoders.rosette.service;

import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.data.mongodb.core.query.Query;

public interface StandardCRUD<T> {

	T create(T data, HttpServletResponse response);
	T read(final String id);
    T read(final String id, boolean checkPermissions);

	List<T> readMany(final Query query);
    List<T> readMany(final Query query, boolean checkPermissions);
	boolean readManyItemFilter(T item);
	
	void update(final String id, HttpServletRequest request, HttpServletResponse response);
	void delete(final String id, HttpServletResponse response);

	void refresh(Set<String> changedCollections);
	
	void setReferences(T data, boolean checkPermissions);
    public Class<?>[] references();
}
