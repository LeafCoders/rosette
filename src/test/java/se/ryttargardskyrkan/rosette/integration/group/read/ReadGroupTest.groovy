package se.ryttargardskyrkan.rosette.integration.group.read

import static junit.framework.Assert.*

import javax.servlet.http.HttpServletResponse

import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpGet
import org.junit.Test

import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.integration.util.TestUtil

import com.mongodb.util.JSON

public class ReadGroupTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given
		String groups = """
		[{
			"_id" : "1",
			"name" : "Admins",
			"description" : "Super users"
		},
		{
			"_id" : "2",
			"name" : "Translators",
			"description" : "All translators",
			"permissions" : ["translatorPermissions"]
		}]
		"""
		mongoTemplate.getCollection("groups").insert(JSON.parse(groups))

		// When
		HttpGet getRequest = new HttpGet(baseUrl + "/groups/2")
		getRequest.setHeader("Accept", "application/json; charset=UTF-8")
		getRequest.setHeader("Content-Type", "application/json; charset=UTF-8")
		HttpResponse response = httpClient.execute(getRequest)

		// Then
		assertEquals(HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode())
		assertEquals("application/json;charset=UTF-8", response.getHeaders("Content-Type")[0].getValue())
		String expectedGroup = """
		{
			"id" : "2",
			"name" : "Translators",
			"description" : "All translators",
			"permissions" : ["translatorPermissions"]
		}
		"""
		TestUtil.assertJsonResponseEquals(expectedGroup, response)
	}
}
