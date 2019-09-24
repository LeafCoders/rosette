package se.leafcoders.rosette.util;

import static org.junit.Assert.assertTrue;
import java.io.IOException;
import org.junit.Test;
import com.fasterxml.jackson.core.JsonProcessingException;

public class IdToSlugConverterTest {

    @Test
    public void convertToIdSlug() throws JsonProcessingException, IOException {
        // converts to slug that starts with name and prefix
        assertTrue(IdToSlugConverter.convertIdToSlug(4711, "This is a name", "ab").startsWith("this-is-a-name-ab"));
        assertTrue(IdToSlugConverter.convertIdToSlug(4711, "title", "cd").startsWith("title-cd"));

        // Converts name that contains invalid URL chars 
        assertTrue(IdToSlugConverter.convertIdToSlug(4711, "ÅÄÖÈÉ/åäöèé!", "pr").startsWith("aaoeeaaoee-pr"));

        // Converts id that is greater than slug sum
        String slugLargeId = IdToSlugConverter.convertIdToSlug(998877, "a", "bt");
        assertTrue(IdToSlugConverter.convertSlugToId(slugLargeId) == 998877);
    }

    @Test
    public void convertSlugToId() throws JsonProcessingException, IOException {
        // Converts from id to slug and back to id
        String slug4711 = IdToSlugConverter.convertIdToSlug(4711, "This is a name", "ar");
        assertTrue(IdToSlugConverter.convertSlugToId(slug4711) == 4711);

        String slug12345 = IdToSlugConverter.convertIdToSlug(12345, "Säg står du i hörnet!", "ct");
        assertTrue(IdToSlugConverter.convertSlugToId(slug12345) == 12345);
    }

}

