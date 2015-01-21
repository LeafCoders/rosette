package se.ryttargardskyrkan.rosette.integration.resourceType

import static org.junit.Assert.assertEquals
import javax.servlet.http.HttpServletResponse
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpDelete
import org.junit.Test
import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.model.resource.ResourceType
import se.ryttargardskyrkan.rosette.model.resource.UserResourceType

public class DeleteResourceTypeTest extends AbstractIntegrationTest {

    @Test
    public void deleteUserResourceTypeWithSuccess() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenGroup(group1)
		givenResourceType(userResourceTypeSingle)
		givenPermissionForUser(user1, ["delete:resourceTypes:${userResourceTypeSingle.id}"])

		// When
		String deleteUrl = "/resourceTypes/${userResourceTypeSingle.id}"
		HttpResponse deleteResponse = whenDelete(deleteUrl, user1)

		// Then
		thenResponseCodeIs(deleteResponse, HttpServletResponse.SC_OK)
		releaseDeleteRequest()
		thenItemsInDatabaseIs(ResourceType.class, 0)
    }

	@Test
	public void failsWhenNothingToDelete() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["delete:resourceTypes"])
		givenGroup(group1)
		givenResourceType(userResourceTypeSingle)
		
		// When
		String deleteUrl = "/resourceTypes/nonExistingKey"
		HttpResponse deleteResponse = whenDelete(deleteUrl, user1)

		// Then
		thenResponseCodeIs(deleteResponse, HttpServletResponse.SC_NOT_FOUND)
		releaseDeleteRequest()
		thenItemsInDatabaseIs(ResourceType.class, 1)
    }


	@Test
	public void failsWhenMissingPermission() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenGroup(group1)
		givenResourceType(userResourceTypeSingle)
		
		// When
		String deleteUrl = "/resourceTypes/${userResourceTypeSingle.id}"
		HttpResponse deleteResponse = whenDelete(deleteUrl, user1)

		// Then
		thenResponseCodeIs(deleteResponse, HttpServletResponse.SC_FORBIDDEN)
		releaseDeleteRequest()
		thenItemsInDatabaseIs(ResourceType.class, 1)
    }
}
