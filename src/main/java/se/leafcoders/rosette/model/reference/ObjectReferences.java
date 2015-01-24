package se.leafcoders.rosette.model.reference;

import java.util.ArrayList;
import se.leafcoders.rosette.model.BaseModel;

public class ObjectReferences<T extends BaseModel> extends ArrayList<T> {
	private static final long serialVersionUID = -283408671553602979L;

	public boolean hasRefs() {
        return !isEmpty();
    }

    public boolean updateRef(T ref) {
    	for (int index = 0; index < size(); ++index)  {
    		if (get(index).getId().equals(ref.getId())) {
    			set(index, ref);
    			return true;
    		}
    	}
    	return false;
    }
}
