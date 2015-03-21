package se.leafcoders.rosette.integration.permission

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.junit.Test;
import com.mongodb.util.JSON;
import se.leafcoders.rosette.integration.AbstractIntegrationTest;
import se.leafcoders.rosette.integration.util.TestUtil;
import se.leafcoders.rosette.model.Permission;

public class CreatePermissionTest extends AbstractIntegrationTest {

	@Test
	public void createPermissionWithSuccess() throws ClientProtocolException, IOException {
        // Given
		givenUser(user1)
		String userPermissionId = givenPermissionForUser(user1, ["create:permissions", "read:users", "read:groups"])
		givenGroup(group1)
		
        // When
		String postUrl = "/permissions"
		HttpResponse postResponse = whenPost(postUrl, user1, """{
			"group" : ${ toJSON(group1) },
			"patterns" : ["*:events"]
		}""")

		// Then
		String responseBody = thenResponseCodeIs(postResponse, HttpServletResponse.SC_CREATED)
		thenResponseHeaderHas(postResponse, "Content-Type", "application/json;charset=UTF-8")

		String expectedResponseData = """{
			"id" : "${ JSON.parse(responseBody)['id'] }",
			"everyone" : null,
			"user" : null,
			"group" : ${ toJSON(group1) },
			"patterns" : ["*:events"]
		}"""
		thenResponseDataIs(responseBody, expectedResponseData)

		
		String expectedDBData = """[
			{
				"id" : "${ userPermissionId }",
				"everyone" : null,
				"user" : ${ toJSON(userRef1) },
				"group" : null,
				"patterns" : ["create:permissions", "read:users", "read:groups"]
			},
			{
				"id" : "${ JSON.parse(responseBody)['id'] }",
				"everyone" : null,
				"user" : null,
				"group" : ${ toJSON(group1) },
				"patterns" : ["*:events"]
			}
		]"""
		thenDataInDatabaseIs(Permission.class, expectedDBData)
		thenItemsInDatabaseIs(Permission.class, 2)
	}

	@Test
	public void failCreatePermissionWhenInvalidContent() throws ClientProtocolException, IOException {
        // Given
		givenUser(user1)
		String userPermissionId = givenPermissionForUser(user1, ["create:permissions", "read:users", "read:groups"])
		givenGroup(group1)
		
        // When
		String postUrl = "/permissions"
		HttpResponse postResponse = whenPost(postUrl, user1, """{
			"patterns" : ["*:events"]
		}""")

		// Then
		thenResponseCodeIs(postResponse, HttpServletResponse.SC_BAD_REQUEST)
		releasePostRequest()

        // When
		postResponse = whenPost(postUrl, user1, """{
			"everyone" : "true",
			"user" : ${ toJSON(userRef1) },
			"patterns" : ["*:events"]
		}""")

		// Then
		thenResponseCodeIs(postResponse, HttpServletResponse.SC_BAD_REQUEST)
		releasePostRequest()
		
		// When
		postResponse = whenPost(postUrl, user1, """{
			"user" : ${ toJSON(userRef1) },
			"group" : ${ toJSON(group1) },
			"patterns" : ["*:events"]
		}""")

		// Then
		thenResponseCodeIs(postResponse, HttpServletResponse.SC_BAD_REQUEST)
	}
}
