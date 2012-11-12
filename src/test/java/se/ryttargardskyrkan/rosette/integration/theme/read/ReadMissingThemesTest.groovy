package se.ryttargardskyrkan.rosette.integration.theme.read

import static junit.framework.Assert.*

import javax.servlet.http.HttpServletResponse

import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpGet
import org.junit.Test

import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest

import com.mongodb.util.JSON

public class ReadMissingThemesTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given
		mongoTemplate.getCollection("themes").insert(JSON.parse("""
		[{
			"_id" : "1",
			"title" : "Markusevangeliet",
			"description" : "Vi läser igenom markusevangeliet"
		},
		{
			"_id" : "2",
			"title" : "Johannesevangeliet",
			"description" : "Vi läser igenom johannesevangeliet"
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
		HttpGet getRequest = new HttpGet(baseUrl + "/themes/4")
		getRequest.setHeader("Accept", "application/json; charset=UTF-8")
		getRequest.setHeader("Content-Type", "application/json; charset=UTF-8")
		HttpResponse response = httpClient.execute(getRequest)

		// Then
		assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatusLine().getStatusCode())
		assertEquals("Not Found", response.getStatusLine().getReasonPhrase())
	}
}
