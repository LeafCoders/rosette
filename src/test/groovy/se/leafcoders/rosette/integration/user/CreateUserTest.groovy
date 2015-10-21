package se.leafcoders.rosette.integration.user

import static org.junit.Assert.*;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.junit.Test;
import com.mongodb.util.JSON;
import se.leafcoders.rosette.integration.AbstractIntegrationTest;
import se.leafcoders.rosette.integration.util.TestUtil;
import se.leafcoders.rosette.model.User;

public class CreateUserTest extends AbstractIntegrationTest {

	@Test
	public void createUserWithSuccess() throws ClientProtocolException, IOException {
        // Given
		givenUser(user1)
		givenPermissionForUser(user1, ["users:create", "users:read"])
		givenLocation(location1)

        // When
		String postUrl = "/users"
		HttpResponse postResponse = whenPost(postUrl, user1, """{
			"email" : "n@is.se",
			"firstName" : "Nisse",
			"lastName" : "Hult",
			"password" : "password"
		}""")

		// Then
		String responseBody = thenResponseCodeIs(postResponse, HttpServletResponse.SC_CREATED)
		thenResponseHeaderHas(postResponse, "Content-Type", "application/json;charset=UTF-8")

		String expectedResponseData = """{
			"id" : "${ JSON.parse(responseBody)['id'] }",
			"email" : "n@is.se",
			"firstName" : "Nisse",
			"lastName" : "Hult",
			"password" : null,
		    "fullName" : "Nisse Hult"
		}"""
		thenResponseDataIs(responseBody, expectedResponseData)
		thenItemsInDatabaseIs(User.class, 2)
		
		// Asserting database
		User userInDatabase = mongoTemplate.findById(JSON.parse(responseBody)['id'], User.class)
		assertTrue(userInDatabase.hashedPassword.startsWith("\$2a\$"))
	}

	@Test
	public void failCreateUserWithoutPermission() throws ClientProtocolException, IOException {
        // Given
		givenUser(user1)
		givenPermissionForUser(user1, ["users:read"])
		givenLocation(location1)

        // When
		String postUrl = "/users"
		HttpResponse postResponse = whenPost(postUrl, user1, """{
			"email" : "n@is.se",
			"firstName" : "Nisse",
			"lastName" : "Hult",
			"password" : "password"
		}""")

		// Then
		thenResponseCodeIs(postResponse, HttpServletResponse.SC_FORBIDDEN)
		thenItemsInDatabaseIs(User.class, 1)
	}

	@Test
	public void failCreateUserWithInvalidContent() throws ClientProtocolException, IOException {
        // Given
		givenUser(user1)
		givenPermissionForUser(user1, ["users:create", "users:read"])
		givenLocation(location1)

        // When
		String postUrl = "/users"
		HttpResponse postResponse = whenPost(postUrl, user1, """{
			"firstName" : "Nisse",
			"description-asdf" : "Hult"
		}""")

		// Then
		thenResponseCodeIs(postResponse, HttpServletResponse.SC_BAD_REQUEST)
		assertEquals("Bad Request", postResponse.getStatusLine().getReasonPhrase())
		thenItemsInDatabaseIs(User.class, 1)
	}
}
