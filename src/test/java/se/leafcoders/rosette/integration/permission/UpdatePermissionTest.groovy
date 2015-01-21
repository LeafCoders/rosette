package se.leafcoders.rosette.integration.permission

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.junit.Test;
import se.leafcoders.rosette.integration.AbstractIntegrationTest;
import se.leafcoders.rosette.model.Permission;

public class UpdatePermissionTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		String userPermissionId = givenPermissionForUser(user1, ["update:permissions", "read:permissions", "read:users"])

		// When
		String putUrl = "/permissions/${ userPermissionId }"
		HttpResponse putResponse = whenPut(putUrl, user1, """{
			"user" : ${ toJSON(user1) },
			"patterns" : ["read:events"]
		}""")

		// Then
		thenResponseCodeIs(putResponse, HttpServletResponse.SC_OK)

		String expectedData = """[{
			"id" : "${ userPermissionId }",
			"everyone" : null,
			"user" : ${ toJSON(user1) },
			"group" : null,
			"patterns" : ["read:events"]
		}]"""
		thenDataInDatabaseIs(Permission.class, expectedData)
		thenItemsInDatabaseIs(Permission.class, 1)
	}
}
