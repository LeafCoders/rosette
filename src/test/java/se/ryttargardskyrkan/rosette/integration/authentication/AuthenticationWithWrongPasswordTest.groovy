package se.ryttargardskyrkan.rosette.integration.authentication

import static junit.framework.Assert.*

import javax.servlet.http.HttpServletResponse

import org.apache.http.HttpResponse
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.auth.BasicScheme
import org.junit.Test
import org.junit.Assert.*

import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.security.RosettePasswordService

import com.mongodb.util.JSON

public class AuthenticationWithWrongPasswordTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenGroup(group1)
		givenGroupMembership(user1, group1)

		// When
		getRequest = new HttpGet(baseUrl + "/authentication")
		getRequest.addHeader(new BasicScheme().authenticate(
			new UsernamePasswordCredentials(user1.username, "invalidPassword"), getRequest));
		HttpResponse getResponse = httpClient.execute(getRequest)

		// Then
		thenResponseCodeIs(getResponse, HttpServletResponse.SC_UNAUTHORIZED)
		assertEquals("Unauthorized", getResponse.getStatusLine().getReasonPhrase())
	}
}
