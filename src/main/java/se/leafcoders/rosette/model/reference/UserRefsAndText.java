package se.leafcoders.rosette.model.reference;

import java.util.ArrayList;
import java.util.List;

public class UserRefsAndText extends ObjectReferencesAndText<UserRef> {
    
    public String namesString() {
        List<String> names = new ArrayList<String>();
        getRefs().forEach((UserRef user) -> {
            names.add(user.getFullName());
        });
        if (getText() != null) {
            names.add(getText());
        }
        return String.join(", ", names);
    }
}
