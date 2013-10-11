package se.ryttargardskyrkan.rosette.integration.permission.read

import static junit.framework.Assert.*

import javax.servlet.http.HttpServletResponse

import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpGet
import org.junit.Test

import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.integration.util.TestUtil

import com.mongodb.util.JSON

public class ReadPermissionTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given
		mongoTemplate.getCollection("permissions").insert(JSON.parse("""
		[{
			"_id" : "1",
			"userId" : "1",
			"patterns" : ["*"]
		},{
			"_id" : "2",
			"everyone" : true,
			"patterns" : ["read:permissions"]
		}]
		"""))

		// When
		HttpGet getRequest = new HttpGet(baseUrl + "/permissions/2")
		getRequest.setHeader("Accept", "application/json; charset=UTF-8")
		getRequest.setHeader("Content-Type", "application/json; charset=UTF-8")
		HttpResponse response = httpClient.execute(getRequest)

		// Then
		assertEquals(HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode())
		assertEquals("application/json;charset=UTF-8", response.getHeaders("Content-Type")[0].getValue())
		String expectedPermission = """
		{
			"id" : "2",
			"everyone" : true,
			"userId" : null,
			"userFullName" : null,
			"groupId" : null,
			"groupName" : null,
			"patterns" : ["read:permissions"]
		}
		"""
		TestUtil.assertJsonResponseEquals(expectedPermission, response)
	}
}
