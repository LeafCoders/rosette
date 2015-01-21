package se.leafcoders.rosette.integration.authentication

import static org.junit.Assert.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.junit.Test;
import se.leafcoders.rosette.integration.AbstractIntegrationTest;

public class AuthenticationWithoutAuthenticationTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenGroup(group1)
		givenGroupMembership(user1, group1)

		// When
		HttpGet getRequest = new HttpGet(baseUrl + "/authentication")
		HttpResponse getResponse = httpClient.execute(getRequest)

		// Then
		thenResponseCodeIs(getResponse, HttpServletResponse.SC_UNAUTHORIZED)
		assertEquals("Unauthorized", getResponse.getStatusLine().getReasonPhrase())

		getRequest.releaseConnection()
	}
}
