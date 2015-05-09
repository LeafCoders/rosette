package se.leafcoders.rosette.service;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.data.mongodb.core.query.Query;

public interface StandardCRUD<T> {

	T create(T data, HttpServletResponse response);
	T read(final String id);

	List<T> readMany(final Query query);
	boolean readManyItemFilter(T item);
	
	void update(final String id, T data, HttpServletResponse response);
	void delete(final String id, HttpServletResponse response);

	void insertDependencies(T data);
}
