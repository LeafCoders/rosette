package se.leafcoders.rosette.integration.auth.forgottenPassword

import static org.junit.Assert.*
import javax.servlet.http.HttpServletResponse
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.junit.Test
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import se.leafcoders.rosette.integration.AbstractIntegrationTest
import se.leafcoders.rosette.model.ForgottenPassword
import se.leafcoders.rosette.model.User

public class ForgottenPasswordTest extends AbstractIntegrationTest {

	@Test
	public void forgottenPasswordWithSuccess() throws ClientProtocolException, IOException {
        // Given
        givenUser(user1)

        // When
		String postUrl = "/forgottenPassword?email=${ user1.email }"
		HttpResponse postResponse = whenPostAuth(postUrl, null, null)

		// Then
		String responseBody = thenResponseCodeIs(postResponse, HttpServletResponse.SC_CREATED)
        assertTrue(responseBody.empty)

		// Asserting database
        thenItemsInDatabaseIs(ForgottenPassword.class, 1)
		ForgottenPassword fp = mongoTemplate.findAll(ForgottenPassword.class).first()
		assertTrue(fp.userId == user1.id)
        assertTrue(!fp.token.empty)

        // When
        String newPassword = "spelaBoll"
        String putUrl = "/forgottenPassword?token=${ fp.token }&password=${ encodePassword(newPassword) }" 
        HttpResponse putResponse = whenPutAuth(putUrl, null, null)

        // Then
        responseBody = thenResponseCodeIs(putResponse, HttpServletResponse.SC_OK)
        assertTrue(responseBody.empty)

        // Asserting database
        thenItemsInDatabaseIs(ForgottenPassword.class, 0)
        User user1NewPassword = mongoTemplate.findById(user1.id, User.class)
        assertTrue(new BCryptPasswordEncoder().matches(newPassword, user1NewPassword.hashedPassword))
	}

    @Test
    public void createForgottenPasswordShouldFailSilent() throws ClientProtocolException, IOException {
        // When
        String postUrl = "/forgottenPassword?email=not@valid.email"
        HttpResponse postResponse = whenPostAuth(postUrl, null, null)

        // Then
        thenResponseCodeIs(postResponse, HttpServletResponse.SC_CREATED)
        thenItemsInDatabaseIs(ForgottenPassword.class, 0)
    }

    @Test
    public void applyForgottenPasswordShouldFail() throws ClientProtocolException, IOException {
        // Given
        givenUser(user1)
        ForgottenPassword fp = givenForgottenPassword(user1)
        
        // When
        String putUrl = "/forgottenPassword?token=123&password=password"
        HttpResponse putResponse = whenPutAuth(putUrl, null, null)

        // Then
        String responseBody = thenResponseCodeIs(putResponse, HttpServletResponse.SC_NOT_FOUND)
        thenResponseDataIs(responseBody, """
            { "error" : "error.notFound", "reason" : "Id (123) of resource type (ForgottenPassword) was not found.", "reasonParams": null }
        """)

        // When
        putUrl = "/forgottenPassword?token=${ fp.token }&password=p+aaa"
        putResponse = whenPutAuth(putUrl, null, null)

        // Then
        responseBody = thenResponseCodeIs(putResponse, HttpServletResponse.SC_BAD_REQUEST)
        thenResponseDataIs(responseBody, """[
            { "property" : "password", "message" : "Password must be base64 url encoded" }
        ]""")
    }
}
