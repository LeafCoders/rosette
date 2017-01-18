package se.leafcoders.rosette.service;

import org.springframework.stereotype.Service;
import se.leafcoders.rosette.model.TextValue;
import se.leafcoders.rosette.security.PermissionType;

@Service
public class TextValueService extends MongoTemplateCRUD<TextValue> {

	public TextValueService() {
		super(TextValue.class, PermissionType.TEXT_VALUES);
	}

    @Override
    public void setReferences(TextValue data, TextValue dataInDb, boolean checkPermissions) {
    }

    @Override
    public Class<?>[] references() {
        return null;
    }
}
