package se.leafcoders.rosette.integration.group

import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.junit.Test;
import se.leafcoders.rosette.integration.AbstractIntegrationTest;
import se.leafcoders.rosette.model.Group;

public class DeleteGroupTest extends AbstractIntegrationTest {

	@Test
	public void deleteGroupWithSuccess() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["delete:groups:${ group1.id }"])
		givenGroup(group1)
		givenGroup(group2)

		// When
		String deleteUrl = "/groups/${ group1.id }"
		HttpResponse deleteResponse = whenDelete(deleteUrl, user1)
		
		// Then
		thenResponseCodeIs(deleteResponse, HttpServletResponse.SC_OK)
		thenItemsInDatabaseIs(Group.class, 1)
	}

	@Test
	public void failDeleteGroupWithoutPermission() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["delete:groups:${ group1.id }"])
		givenGroup(group1)
		givenGroup(group2)

		// When
		String deleteUrl = "/groups/${ group2.id }"
		HttpResponse deleteResponse = whenDelete(deleteUrl, user1)
		
		// Then
		thenResponseCodeIs(deleteResponse, HttpServletResponse.SC_FORBIDDEN)
		thenItemsInDatabaseIs(Group.class, 2)
	}

	@Test
	public void failDeleteGroupThatDontExist() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["delete:groups"])
		givenGroup(group1)
		givenGroup(group2)

		// When
		String deleteUrl = "/groups/4711"
		HttpResponse deleteResponse = whenDelete(deleteUrl, user1)
		
		// Then
		thenResponseCodeIs(deleteResponse, HttpServletResponse.SC_NOT_FOUND)
		thenItemsInDatabaseIs(Group.class, 2)
	}
}
