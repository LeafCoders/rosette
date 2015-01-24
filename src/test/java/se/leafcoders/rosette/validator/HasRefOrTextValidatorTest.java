package se.leafcoders.rosette.validator;

import org.junit.Test;
import se.leafcoders.rosette.model.Location;
import se.leafcoders.rosette.model.reference.ObjectReferenceOrText;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class HasRefOrTextValidatorTest {
	
    @Test
    public void testHasNone() {
        HasRefOrTextValidator validator = new HasRefOrTextValidator();
        ObjectReferenceOrText<Location> hasNone = new ObjectReferenceOrText<Location>();
        assertFalse(validator.isValid(hasNone, null));
    }

    @Test
    public void testHasRef() {
        HasRefOrTextValidator validator = new HasRefOrTextValidator();
        ObjectReferenceOrText<Location> hasRef = new ObjectReferenceOrText<Location>();
        Location location = new Location();
        location.setId("Some id");
        hasRef.setRef(location);
        assertTrue(validator.isValid(hasRef, null));
    }

    @Test
    public void testHasText() {
        HasRefOrTextValidator validator = new HasRefOrTextValidator();
        ObjectReferenceOrText<Location> hasText = new ObjectReferenceOrText<Location>();
        hasText.setText("TestText");
        assertTrue(validator.isValid(hasText, null));
    }

    @Test
    public void testHasIdAndText() {
        HasRefOrTextValidator validator = new HasRefOrTextValidator();
        ObjectReferenceOrText<Location> hasBoth = new ObjectReferenceOrText<Location>();
        Location location = new Location();
        location.setId("Some id");
        hasBoth.setRef(location);
        hasBoth.setText("TestText");
        assertFalse(validator.isValid(hasBoth, null));
    }
}
