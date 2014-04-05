package se.ryttargardskyrkan.rosette.integration.location.read

import com.mongodb.util.JSON
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpGet
import org.junit.Test
import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.integration.util.TestUtil

import javax.servlet.http.HttpServletResponse

import static junit.framework.Assert.assertEquals

public class ReadLocationTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given
		mongoTemplate.getCollection("locations").insert(JSON.parse("""
        [{
			"_id" : "1",
            "name" : "Kyrksalen",
			"description" : "En stor lokal med plats för ca 700 pers."
		},
		{
			"_id" : "2",
            "name" : "Oasen",
			"description" : "Konferensrum för ca 50 pers."
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
		HttpGet getRequest = new HttpGet(baseUrl + "/locations/2")
		getRequest.setHeader("Accept", "application/json; charset=UTF-8")
		getRequest.setHeader("Content-Type", "application/json; charset=UTF-8")
		HttpResponse response = httpClient.execute(getRequest)

		// Then
		assertEquals(HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode())
		assertEquals("application/json;charset=UTF-8", response.getHeaders("Content-Type")[0].getValue())
		String expectedLocation = """
		{
			"id" : "2",
            "name" : "Oasen",
			"description" : "Konferensrum för ca 50 pers.",
			"directionImage": null
		}
		"""
		TestUtil.assertJsonResponseEquals(expectedLocation, response)
	}
}
