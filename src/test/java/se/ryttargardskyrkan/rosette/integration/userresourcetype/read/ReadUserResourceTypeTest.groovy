package se.ryttargardskyrkan.rosette.integration.userresourcetype.read

import com.mongodb.util.JSON
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpGet
import org.junit.Test
import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.integration.util.TestUtil

import javax.servlet.http.HttpServletResponse

import static junit.framework.Assert.assertEquals

public class ReadUserResourceTypeTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given
		mongoTemplate.getCollection("userResourceTypes").insert(JSON.parse("""
        [{
			"_id" : "1",
            "name" : "motesledare",
			"groupId" : "1"
		},
		{
			"_id" : "2",
            "name" : "tolkar",
			"groupId" : "2"
		}]
        """))
		
		mongoTemplate.getCollection("permissions").insert(JSON.parse("""
		[{
			"_id" : "1",
			"everyone" : true,
			"patterns" : ["*"]
		}]
		"""));

		// When
		HttpGet getRequest = new HttpGet(baseUrl + "/userResourceTypes/2")
		getRequest.setHeader("Accept", "application/json; charset=UTF-8")
		getRequest.setHeader("Content-Type", "application/json; charset=UTF-8")
		HttpResponse response = httpClient.execute(getRequest)

		// Then
		assertEquals(HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode())
		assertEquals("application/json;charset=UTF-8", response.getHeaders("Content-Type")[0].getValue())
		String expectedUserResourceType = """
		{
			"id" : "2",
            "name" : "tolkar",
			"groupId" : "2",
			"sortOrder" : 0
		}
		"""
		TestUtil.assertJsonResponseEquals(expectedUserResourceType, response)
	}
}
