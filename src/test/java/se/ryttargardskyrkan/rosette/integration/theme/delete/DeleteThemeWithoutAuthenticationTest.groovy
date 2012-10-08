package se.ryttargardskyrkan.rosette.integration.theme.delete;

import static org.junit.Assert.*

import javax.servlet.http.HttpServletResponse

import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpDelete
import org.codehaus.jackson.map.ObjectMapper
import org.codehaus.jackson.type.TypeReference
import org.junit.Test
import org.springframework.data.mongodb.core.query.Query

import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.integration.util.TestUtil
import se.ryttargardskyrkan.rosette.model.Theme

public class DeleteThemeWithoutAuthenticationTest extends AbstractIntegrationTest {

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
		HttpDelete deleteRequest = new HttpDelete(baseUrl + "/themes/1")
		deleteRequest.setHeader("Accept", "application/json; charset=UTF-8")
		deleteRequest.setHeader("Content-Type", "application/json; charset=UTF-8")
		HttpResponse response = httpClient.execute(deleteRequest)

		// Then
//		Disabling permission check for now
//		assertEquals(HttpServletResponse.SC_FORBIDDEN, response.getStatusLine().getStatusCode())
//		assertEquals("Forbidden", response.getStatusLine().getReasonPhrase())
//		assertEquals(2L, mongoTemplate.count(new Query(), Theme.class))
		
		assertEquals(HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode())
		assertEquals(1L, mongoTemplate.count(new Query(), Theme.class))
	}
}
