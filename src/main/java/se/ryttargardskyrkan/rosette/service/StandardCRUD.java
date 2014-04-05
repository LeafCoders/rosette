package se.ryttargardskyrkan.rosette.service;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public interface StandardCRUD<T> {

	T create(T data, HttpServletResponse response);
	T read(final String id);
	List<T> readMany(final Query query);
	void update(final String id, T data, final Update update, HttpServletResponse response);
	void delete(final String id, HttpServletResponse response);
	
	void insertDependencies(T data);
}
