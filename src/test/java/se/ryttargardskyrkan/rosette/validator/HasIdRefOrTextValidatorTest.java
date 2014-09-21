package se.ryttargardskyrkan.rosette.validator;

import org.junit.Test;
import se.ryttargardskyrkan.rosette.model.Location;
import se.ryttargardskyrkan.rosette.model.ObjectReferenceOrText;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class HasIdRefOrTextValidatorTest {
	
    @Test
    public void testHasNone() {
        HasIdRefOrTextValidator validator = new HasIdRefOrTextValidator();
        ObjectReferenceOrText<Location> hasNone = new ObjectReferenceOrText<Location>();
        assertFalse(validator.isValid(hasNone, null));
    }

    @Test
    public void testHasIdRef() {
        HasIdRefOrTextValidator validator = new HasIdRefOrTextValidator();
        ObjectReferenceOrText<Location> hasIdRef = new ObjectReferenceOrText<Location>();
        hasIdRef.setIdRef("TestId");
        assertTrue(validator.isValid(hasIdRef, null));
    }

    @Test
    public void testHasText() {
        HasIdRefOrTextValidator validator = new HasIdRefOrTextValidator();
        ObjectReferenceOrText<Location> hasText = new ObjectReferenceOrText<Location>();
        hasText.setText("TestText");
        assertTrue(validator.isValid(hasText, null));
    }

    @Test
    public void testHasIdAndText() {
        HasIdRefOrTextValidator validator = new HasIdRefOrTextValidator();
        ObjectReferenceOrText<Location> hasBoth = new ObjectReferenceOrText<Location>();
        hasBoth.setIdRef("TestId");
        hasBoth.setText("TestText");
        assertFalse(validator.isValid(hasBoth, null));
    }
}
