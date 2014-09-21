package se.ryttargardskyrkan.rosette.integration.eventType

import com.mongodb.util.JSON
import org.apache.http.HttpResponse
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpDelete
import org.apache.http.impl.auth.BasicScheme
import org.codehaus.jackson.map.ObjectMapper
import org.junit.Test
import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.integration.util.TestUtil
import se.ryttargardskyrkan.rosette.model.EventType
import se.ryttargardskyrkan.rosette.security.RosettePasswordService

import javax.servlet.http.HttpServletResponse

import static junit.framework.Assert.assertEquals

public class DeleteEventTypeTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["delete:eventTypes"])
		givenGroup(group1)
		givenResourceType(userResourceType1)
		givenResourceType(uploadResourceType1)
		givenEventType(eventType1)
		givenEventType(eventType2)

		// Then		
		deleteRequest = new HttpDelete(baseUrl + "/eventTypes/${eventType1.id}")
		HttpResponse uploadResponse = whenDelete(deleteRequest, user1)
		deleteRequest.releaseConnection()

		// Then
		thenResponseCodeIs(uploadResponse, HttpServletResponse.SC_OK)
		thenItemsInDatabaseIs(EventType.class, 1)
	}
}
