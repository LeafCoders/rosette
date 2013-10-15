package se.ryttargardskyrkan.rosette.integration.authorization

import com.mongodb.util.JSON
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpGet
import org.junit.Test
import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.integration.util.TestUtil

import javax.servlet.http.HttpServletResponse

import static junit.framework.Assert.assertEquals
import se.ryttargardskyrkan.rosette.security.RosettePasswordService
import org.apache.http.impl.auth.BasicScheme
import org.apache.http.auth.UsernamePasswordCredentials

public class AuthorizationTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given
        String hashedPassword = new RosettePasswordService().encryptPassword("password");
        mongoTemplate.getCollection("users").insert(JSON.parse("""
		[{
			"_id" : "1",
			"username" : "lars.arvidsson@gmail.com",
			"hashedPassword" : "${hashedPassword}",
			"status" : "active"
		}]
		"""));

		mongoTemplate.getCollection("permissions").insert(JSON.parse("""
		[{
			"_id" : "1",
			"userId" : "1",
			"patterns" : ["read:events"]
		}]
		"""));

		// When
		HttpGet getRequest = new HttpGet(baseUrl + "/authorizations?permissions=read:events,update:events,%20read:events")
		getRequest.setHeader("Accept", "application/json; charset=UTF-8")
		getRequest.setHeader("Content-Type", "application/json; charset=UTF-8")
        getRequest.addHeader(new BasicScheme().authenticate(new UsernamePasswordCredentials("lars.arvidsson@gmail.com", "password"), getRequest))
		HttpResponse response = httpClient.execute(getRequest)

		// Then
		assertEquals(HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode())
		assertEquals("application/json;charset=UTF-8", response.getHeaders("Content-Type")[0].getValue())
		String expectedPermissions = """
		{
		    "read:events" : true,
		    "update:events" : false
		}
		"""
		TestUtil.assertJsonResponseEquals(expectedPermissions, response)
	}
}
