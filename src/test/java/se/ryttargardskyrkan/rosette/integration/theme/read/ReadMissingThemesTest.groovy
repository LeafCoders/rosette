package se.ryttargardskyrkan.rosette.integration.theme.read

import static org.junit.Assert.*

import javax.servlet.http.HttpServletResponse

import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpGet
import org.codehaus.jackson.map.ObjectMapper
import org.codehaus.jackson.type.TypeReference
import org.junit.Test
import org.springframework.data.mongodb.core.MongoTemplate

import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.integration.util.TestUtil
import se.ryttargardskyrkan.rosette.model.Theme

public class ReadMissingThemesTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given
		String themes = """
		[{
			"id" : "1",
			"title" : "Markusevangeliet",
			"description" : "Vi läser igenom markusevangeliet"
		},
		{
			"id" : "2",
			"title" : "Johannesevangeliet",
			"description" : "Vi läser igenom johannesevangeliet"
		}]
		"""
		mongoTemplate.insert(new ObjectMapper().readValue(themes, new TypeReference<ArrayList<Theme>>() {}), "themes")

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
