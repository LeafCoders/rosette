package util;

import org.bson.types.ObjectId;

public class QueryId {

	static public Object get(String id) {
		if (ObjectId.isValid(id)) {
			return new ObjectId(id);
		} else {
			return id;
		}
	}
}
