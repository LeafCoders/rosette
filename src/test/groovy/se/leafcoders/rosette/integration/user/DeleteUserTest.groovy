package se.leafcoders.rosette.integration.user;

import javax.servlet.http.HttpServletResponse
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.junit.Test
import se.leafcoders.rosette.integration.AbstractIntegrationTest
import se.leafcoders.rosette.model.User

public class DeleteUserTest extends AbstractIntegrationTest {

	@Test
	public void deleteOneUserWithSuccess() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenUser(user2)
		givenPermissionForUser(user1, ["users:delete:${ user2.id }"])

		// When
		String deleteUrl = "/users/${ user2.id }"
		HttpResponse deleteResponse = whenDelete(deleteUrl, user1)
		
		// Then
		thenResponseCodeIs(deleteResponse, HttpServletResponse.SC_OK)
		thenItemsInDatabaseIs(User.class, 1)
	}

	@Test
	public void failWhenDeleteUserWithoutPermission() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenUser(user2)
		givenPermissionForUser(user1, ["delete:user:4711"])

		// When
		String deleteUrl = "/users/${ user1.id }"
		HttpResponse deleteResponse = whenDelete(deleteUrl, user1)
		
		// Then
		thenResponseCodeIs(deleteResponse, HttpServletResponse.SC_FORBIDDEN)
		thenItemsInDatabaseIs(User.class, 2)
	}

    @Test
    public void failWhenReferencesByGroupMembership() throws ClientProtocolException, IOException {
        // Given
        givenUser(user1)
        givenGroup(group1)
        givenGroupMembership(user1, group1)
        givenPermissionForUser(user1, ["users:delete:${ user1.id }"])

        // When
        String deleteUrl = "/users/${ user1.id }"
        HttpResponse deleteResponse = whenDelete(deleteUrl, user1)

        // Then
        String responseBody = thenResponseCodeIs(deleteResponse, HttpServletResponse.SC_FORBIDDEN)
        thenResponseDataIs(responseBody, """{
            "error": "error.forbidden",
            "reason": "error.referencedBy",
            "reasonParams": ["GroupMembership"]
        }""")

        thenItemsInDatabaseIs(User.class, 1)
    }

    @Test
    public void failWhenReferencesByResourceInEvent() throws ClientProtocolException, IOException {
        // Given
        givenUser(user1)
        givenLocation(location1)
        givenEventType(eventType1)
        givenResourceType(userResourceTypeSingle)
        givenResourceType(uploadResourceTypeSingle)
        givenEvent(event1)
        givenPermissionForUser(user1, ["users:delete:${ user1.id }"])

        // When
        String deleteUrl = "/users/${ user1.id }"
        HttpResponse deleteResponse = whenDelete(deleteUrl, user1)

        // Then
        String responseBody = thenResponseCodeIs(deleteResponse, HttpServletResponse.SC_FORBIDDEN)
        thenResponseDataIs(responseBody, """{
            "error": "error.forbidden",
            "reason": "error.referencedBy",
            "reasonParams": ["Event"]
        }""")

        thenItemsInDatabaseIs(User.class, 1)
    }

}
