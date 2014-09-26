package se.ryttargardskyrkan.rosette.integration.permission

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.junit.Test;
import com.mongodb.util.JSON;
import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest;
import se.ryttargardskyrkan.rosette.integration.util.TestUtil;
import se.ryttargardskyrkan.rosette.model.Permission;

public class CreatePermissionTest extends AbstractIntegrationTest {

	@Test
	public void createPermissionWithSuccess() throws ClientProtocolException, IOException {
        // Given
		givenUser(user1)
		String userPermissionId = givenPermissionForUser(user1, ["create:permissions"])
		givenGroup(group1)
		
        // When
		String postUrl = "/permissions"
		HttpResponse postResponse = whenPost(postUrl, user1, """{
			"group" : { "idRef" : "${ group1.id }" },
			"patterns" : ["*:events"]
		}""")

		// Then
		String responseBody = thenResponseCodeIs(postResponse, HttpServletResponse.SC_CREATED)
		thenResponseHeaderHas(postResponse, "Content-Type", "application/json;charset=UTF-8")

		String expectedResponseData = """{
			"id" : "${ JSON.parse(responseBody)['id'] }",
			"everyone" : null,
			"user" : null,
			"group" : { "idRef" : "${ group1.id }", "referredObject" : null },
			"patterns" : ["*:events"]
		}"""
		thenResponseDataIs(responseBody, expectedResponseData)

		
		String expectedDBData = """[
			{
				"id" : "${ userPermissionId }",
				"everyone" : null,
				"user" : { "idRef" : "${ user1.id }", "referredObject" : null },
				"group" : null,
				"patterns" : ["create:permissions"]
			},
			{
				"id" : "${ JSON.parse(responseBody)['id'] }",
				"everyone" : null,
				"user" : null,
				"group" : { "idRef" : "${ group1.id }", "referredObject" : null },
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
		String userPermissionId = givenPermissionForUser(user1, ["create:permissions"])
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
			"user" : { "idRef" : "${ user1.id }",
			"patterns" : ["*:events"]
		}""")

		// Then
		thenResponseCodeIs(postResponse, HttpServletResponse.SC_BAD_REQUEST)
		releasePostRequest()
		
		// When
		postResponse = whenPost(postUrl, user1, """{
			"user" : { "idRef" : "${ user1.id }",
			"group" : { "idRef" : "${ group1.id }",
			"patterns" : ["*:events"]
		}""")

		// Then
		thenResponseCodeIs(postResponse, HttpServletResponse.SC_BAD_REQUEST)
	}
}
