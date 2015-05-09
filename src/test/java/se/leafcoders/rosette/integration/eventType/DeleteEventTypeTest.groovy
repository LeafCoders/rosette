package se.leafcoders.rosette.integration.eventType

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.junit.Test;
import se.leafcoders.rosette.integration.AbstractIntegrationTest;
import se.leafcoders.rosette.model.EventType;

public class DeleteEventTypeTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["eventTypes:delete"])
		givenGroup(group1)
		givenResourceType(userResourceTypeSingle)
		givenResourceType(uploadResourceTypeSingle)
		givenEventType(eventType1)
		givenEventType(eventType2)

		// Then		
		String deleteUrl = "/eventTypes/${eventType1.id}"
		HttpResponse uploadResponse = whenDelete(deleteUrl, user1)
		releaseDeleteRequest()

		// Then
		thenResponseCodeIs(uploadResponse, HttpServletResponse.SC_OK)
		thenItemsInDatabaseIs(EventType.class, 1)
	}
}
