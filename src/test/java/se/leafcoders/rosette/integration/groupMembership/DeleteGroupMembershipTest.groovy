package se.leafcoders.rosette.integration.groupMembership

import static junit.framework.Assert.*

import javax.servlet.http.HttpServletResponse

import org.apache.http.HttpResponse
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpDelete
import org.apache.http.impl.auth.BasicScheme
import org.codehaus.jackson.map.ObjectMapper
import org.junit.Test

import se.leafcoders.rosette.integration.AbstractIntegrationTest
import se.leafcoders.rosette.integration.util.TestUtil
import se.leafcoders.rosette.model.GroupMembership
import se.leafcoders.rosette.security.RosettePasswordService

import com.mongodb.util.JSON

public class DeleteGroupMembershipTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenUser(user2)
		givenPermissionForUser(user1, ["delete:groupMemberships"])
		String groupMemb1 = givenGroupMembership(user1, group1)
		String groupMemb2 = givenGroupMembership(user2, group1)

		// Then		
		String deleteUrl = "/groupMemberships/${groupMemb2}"
		HttpResponse uploadResponse = whenDelete(deleteUrl, user1)
		releaseDeleteRequest()

		// Then
		thenResponseCodeIs(uploadResponse, HttpServletResponse.SC_OK)
		thenItemsInDatabaseIs(GroupMembership.class, 1)
	}
}
