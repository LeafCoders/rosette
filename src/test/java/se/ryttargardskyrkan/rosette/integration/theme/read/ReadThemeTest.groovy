package se.ryttargardskyrkan.rosette.integration.theme.read

import static junit.framework.Assert.*

import javax.servlet.http.HttpServletResponse

import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpGet
import org.codehaus.jackson.map.ObjectMapper
import org.codehaus.jackson.type.TypeReference
import org.junit.Test

import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.integration.util.TestUtil

import com.mongodb.util.JSON

public class ReadThemeTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given
		mongoTemplate.getCollection("themes").insert(JSON.parse("""
		[{
			"_id" : "1",
			"title" : "Tema 1",
			"description" : "Beskrivning av tema 1"
		},
		{
			"_id" : "2",
			"title" : "Tema 2",
			"description" : "Beskrivning av tema 2"
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
		HttpGet getRequest = new HttpGet(baseUrl + "/themes/2")
		getRequest.setHeader("Accept", "application/json; charset=UTF-8")
		getRequest.setHeader("Content-Type", "application/json; charset=UTF-8")
		HttpResponse response = httpClient.execute(getRequest)

		// Then
		assertEquals(HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode())
		assertEquals("application/json;charset=UTF-8", response.getHeaders("Content-Type")[0].getValue())
		String expectedTheme = """
		{
			"id" : "2",
			"title" : "Tema 2",
			"description" : "Beskrivning av tema 2"
		}
		"""
		TestUtil.assertJsonResponseEquals(expectedTheme, response)
	}
}
