package se.leafcoders.rosette.integration.location

import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.junit.Test
import se.leafcoders.rosette.integration.AbstractIntegrationTest
import se.leafcoders.rosette.model.Location
import java.io.IOException;
import javax.servlet.http.HttpServletResponse

public class DeleteLocationTest extends AbstractIntegrationTest {

	@Test
	public void successWhenDelete() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["locations:delete:${ location1.id }"])
		givenUploadFolder(uploadFolderLocations)
		givenLocation(location1)

		// When
		String deleteUrl = "/locations/${ location1.id }"
		HttpResponse deleteResponse = whenDelete(deleteUrl, user1)

		// Then
		thenResponseCodeIs(deleteResponse, HttpServletResponse.SC_OK)
		thenItemsInDatabaseIs(Location.class, 0)
	}
    
    @Test
    public void failWhenReferencesByBooking() throws ClientProtocolException, IOException {
        // Given
        givenUser(user1)
        givenPermissionForUser(user1, ["locations:delete:${ location1.id }"])
        givenUploadFolder(uploadFolderLocations)
        givenLocation(location1)
        givenBooking(booking1) // Has reference to location1
        
        // When
        String deleteUrl = "/locations/${ location1.id }"
        HttpResponse deleteResponse = whenDelete(deleteUrl, user1)

        // Then
        String responseBody = thenResponseCodeIs(deleteResponse, HttpServletResponse.SC_FORBIDDEN)
        thenResponseDataIs(responseBody, """{
            "error": "error.forbidden",
            "reason": "error.referencedBy",
            "reasonParams": ["Booking"]
        }""")

        thenItemsInDatabaseIs(Location.class, 1)
    }

}
