package se.ryttargardskyrkan.rosette.integration.user

import static org.junit.Assert.*;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.junit.Test;
import com.mongodb.util.JSON;
import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest;
import se.ryttargardskyrkan.rosette.integration.util.TestUtil;
import se.ryttargardskyrkan.rosette.model.User;
import se.ryttargardskyrkan.rosette.security.RosettePasswordService;

public class CreateUserTest extends AbstractIntegrationTest {

	@Test
	public void createUserWithSuccess() throws ClientProtocolException, IOException {
        // Given
		givenUser(user1)
		givenPermissionForUser(user1, ["create:users"])
		givenLocation(location1)

        // When
		String postUrl = "/users"
		HttpResponse postResponse = whenPost(postUrl, user1, """{
			"username" : "hubbe",
			"firstName" : "Nisse",
			"lastName" : "Hult",
			"password" : "password"
		}""")

		// Then
		String responseBody = thenResponseCodeIs(postResponse, HttpServletResponse.SC_CREATED)
		thenResponseHeaderHas(postResponse, "Content-Type", "application/json;charset=UTF-8")

		String expectedResponseData = """{
			"id" : "${ JSON.parse(responseBody)['id'] }",
			"username" : "hubbe",
			"firstName" : "Nisse",
			"lastName" : "Hult",
			"status" : "active",
			"password" : null,
		    "fullName" : "Nisse Hult"
		}"""
		thenResponseDataIs(responseBody, expectedResponseData)
		thenItemsInDatabaseIs(User.class, 2)
		
		// Asserting database
		User userInDatabase = mongoTemplate.findById(JSON.parse(responseBody)['id'], User.class)
		assertTrue(userInDatabase.hashedPassword.startsWith("\$shiro1\$SHA-256\$"))
	}

	@Test
	public void failCreateUserWithoutPermission() throws ClientProtocolException, IOException {
        // Given
		givenUser(user1)
		givenPermissionForUser(user1, ["read:users"])
		givenLocation(location1)

        // When
		String postUrl = "/users"
		HttpResponse postResponse = whenPost(postUrl, user1, """{
			"username" : "hubbe",
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
		givenPermissionForUser(user1, ["create:users"])
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
