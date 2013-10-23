package se.ryttargardskyrkan.rosette.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import se.ryttargardskyrkan.rosette.model.IdOrText;

public class HasIdOrTextValidatorTest {
	
	@Test
	public void testAllCombinations() {
		HasIdOrTextValidator validator = new HasIdOrTextValidator();
		IdOrText hasNone = new IdOrText();
		assertFalse(validator.isValid(hasNone, null));

		IdOrText hasIdRef = new IdOrText();
		hasIdRef.setIdRef("TestIdRef");
		assertTrue(validator.isValid(hasIdRef, null));

		IdOrText hasText = new IdOrText();
		hasText.setText("TestText");
		assertTrue(validator.isValid(hasText, null));

		IdOrText hasBoth = new IdOrText();
		hasBoth.setIdRef("TestIdRef");
		hasBoth.setText("TestText");
		assertFalse(validator.isValid(hasBoth, null));
	}
}
