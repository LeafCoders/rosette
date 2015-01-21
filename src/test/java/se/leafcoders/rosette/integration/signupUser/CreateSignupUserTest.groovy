package se.leafcoders.rosette.integration.signupUser

import static org.junit.Assert.*;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.junit.Test;
import com.mongodb.util.JSON;
import se.leafcoders.rosette.integration.AbstractIntegrationTest;
import se.leafcoders.rosette.integration.util.TestUtil;
import se.leafcoders.rosette.model.SignupUser;
import se.leafcoders.rosette.security.RosettePasswordService;

public class CreateSignupUserTest extends AbstractIntegrationTest {

	@Test
	public void createSignupUserWithSuccess() throws ClientProtocolException, IOException {
        // Given

        // When
		String postUrl = "/signupUsers"
		HttpResponse postResponse = whenPost(postUrl, null, """{
			"username" : "hubbe",
			"firstName" : "Nisse",
			"lastName" : "Hult",
			"email" : "n@is.se",
			"password" : "password",
			"permissions" : "All please"
		}""")

		// Then
		String responseBody = thenResponseCodeIs(postResponse, HttpServletResponse.SC_CREATED)
		thenResponseHeaderHas(postResponse, "Content-Type", "application/json;charset=UTF-8")

		thenItemsInDatabaseIs(SignupUser.class, 1)
		
		// Asserting database
		SignupUser userInDatabase = mongoTemplate.findById(JSON.parse(responseBody)['id'], SignupUser.class)
		assertTrue(userInDatabase.hashedPassword.startsWith("\$shiro1\$SHA-256\$"))
	}

	@Test
	public void failCreateAnotherSignupUserBeforeTimeout() throws ClientProtocolException, IOException {
        // Given

        // When
		String postUrl = "/signupUsers"
		HttpResponse postResponse1 = whenPost(postUrl, null, """{
			"username" : "hubbe1",
			"firstName" : "Nisse1",
			"lastName" : "Hult1",
			"email" : "n1@is.se",
			"password" : "password",
			"permissions" : "Perms for hubbe1"
		}""")

		thenResponseCodeIs(postResponse1, HttpServletResponse.SC_CREATED)
		thenItemsInDatabaseIs(SignupUser.class, 1)
		
		HttpResponse postResponse2 = whenPost(postUrl, null, """{
			"username" : "hubbe2",
			"firstName" : "Nisse2",
			"lastName" : "Hult2",
			"email" : "n2@is.se",
			"password" : "password",
			"permissions" : "Perms for hubbe2"
		}""")

		// Then
		thenResponseCodeIs(postResponse2, 429) // "Too Many Requests"
		thenItemsInDatabaseIs(SignupUser.class, 1)
	}
}
