package se.ryttargardskyrkan.rosette.integration.theme.read

import static org.junit.Assert.*

import javax.servlet.http.HttpServletResponse

import org.apache.http.Header
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

public class ReadPaginatedThemesTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given
		String themes = """
		[{
			"id" : "1",
			"title" : "Tema 1",
			"description" : "Beskrivning av tema 1"
		},
		{
			"id" : "2",
			"title" : "Tema 3",
			"description" : "Beskrivning av tema 3"
		},
		{
			"id" : "3",
			"title" : "Tema 3",
			"description" : "Beskrivning av tema 3"
		},
		{
			"id" : "4",
			"title" : "Tema 4",
			"description" : "Beskrivning av tema 4"
		},
		{
			"id" : "5",
			"title" : "Tema 5",
			"description" : "Beskrivning av tema 5"
		},
		{
			"id" : "6",
			"title" : "Tema 6",
			"description" : "Beskrivning av tema 6"
		}]
		"""
		mongoTemplate.insert(new ObjectMapper().readValue(themes, new TypeReference<ArrayList<Theme>>() {}), "themes")

		// When
		HttpGet getRequest = new HttpGet(baseUrl + "/themes?page=2&per_page=2")
		getRequest.setHeader("Accept", "application/json; charset=UTF-8")
		getRequest.setHeader("Content-Type", "application/json; charset=UTF-8")
		HttpResponse response = httpClient.execute(getRequest)

		// Then
		assertEquals(HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode())
		assertEquals("application/json;charset=UTF-8", response.getHeaders("Content-Type")[0].getValue())
		String exptectedThemes = """
		[{
			"id" : "3",
			"title" : "Tema 3",
			"description" : "Beskrivning av tema 3"
		},
		{
			"id" : "4",
			"title" : "Tema 4",
			"description" : "Beskrivning av tema 4"
		}]
		"""
		TestUtil.assertJsonResponseEquals(exptectedThemes, response)
		
		StringBuilder sb = new StringBuilder()
		sb.append("<themes?page=1&per_page=2>; rel=\"previous\"")
		sb.append(",")
		sb.append("<themes?page=3&per_page=2>; rel=\"next\"")
		Header linkHeader = response.getFirstHeader("Link")
		assertEquals(sb.toString(), linkHeader.getValue())
	}
}
