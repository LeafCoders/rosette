package se.leafcoders.rosette.integration.location

import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.junit.Test
import se.leafcoders.rosette.integration.AbstractIntegrationTest
import se.leafcoders.rosette.model.Location

import javax.servlet.http.HttpServletResponse

public class DeleteLocationTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["delete:locations:${ location1.id }"])
		givenLocation(location1)

		// When
		String deleteUrl = "/locations/${ location1.id }"
		HttpResponse deleteResponse = whenDelete(deleteUrl, user1)

		// Then
		thenResponseCodeIs(deleteResponse, HttpServletResponse.SC_OK)
		thenItemsInDatabaseIs(Location.class, 0)
	}
}
