package se.ryttargardskyrkan.rosette.integration.authentication

import javax.servlet.http.HttpServletResponse
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpGet
import org.junit.Test
import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest

public class AuthenticationTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenGroup(group1)
		givenGroupMembership(user1, group1)

		// When
		getRequest = new HttpGet(baseUrl + "/authentication")
		HttpResponse getResponse = whenGet(getRequest, user1)

		// Then
		thenResponseCodeIs(getResponse, HttpServletResponse.SC_OK)
	}
}
