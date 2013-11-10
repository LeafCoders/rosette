package se.ryttargardskyrkan.rosette.validator;

import org.junit.Test;
import se.ryttargardskyrkan.rosette.model.Location;
import se.ryttargardskyrkan.rosette.model.ObjectReference;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class HasIdRefOrTextValidatorTest {
	
    @Test
    public void testHasNone() {
        HasIdRefOrTextValidator validator = new HasIdRefOrTextValidator();
        ObjectReference<Location> hasNone = new ObjectReference<Location>();
        assertFalse(validator.isValid(hasNone, null));
    }

    @Test
    public void testHasIdRef() {
        HasIdRefOrTextValidator validator = new HasIdRefOrTextValidator();
        ObjectReference<Location> hasIdRef = new ObjectReference<Location>();
        hasIdRef.setIdRef("TestId");
        assertTrue(validator.isValid(hasIdRef, null));
    }

    @Test
    public void testHasText() {
        HasIdRefOrTextValidator validator = new HasIdRefOrTextValidator();
        ObjectReference<Location> hasText = new ObjectReference<Location>();
        hasText.setText("TestText");
        assertTrue(validator.isValid(hasText, null));
    }

    @Test
    public void testHasIdAndText() {
        HasIdRefOrTextValidator validator = new HasIdRefOrTextValidator();
        ObjectReference<Location> hasBoth = new ObjectReference<Location>();
        hasBoth.setIdRef("TestId");
        hasBoth.setText("TestText");
        assertFalse(validator.isValid(hasBoth, null));
    }
}
