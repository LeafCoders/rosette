package se.ryttargardskyrkan.rosette.integration.theme.create

import static org.junit.Assert.*

import javax.servlet.http.HttpServletResponse

import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.codehaus.jackson.map.ObjectMapper
import org.junit.Test
import org.springframework.data.mongodb.core.query.Query

import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.integration.util.TestUtil
import se.ryttargardskyrkan.rosette.model.Theme

public class CreateThemeWithoutAuthenticationTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given
		
		// When
		HttpPost postRequest = new HttpPost(baseUrl + "/themes")
		String requestBody = """
		{
			"title" : "Markusevangeliet",
			"description" : "Vi läser igenom markusevangeliet"
		}
		"""
		postRequest.setEntity(new StringEntity(requestBody, "application/json", "UTF-8"))
		HttpResponse response = httpClient.execute(postRequest)

		// Then
//		Disabling permission check for now
//		assertEquals(HttpServletResponse.SC_FORBIDDEN, response.getStatusLine().getStatusCode())
//		assertEquals("Forbidden", response.getStatusLine().getReasonPhrase())
//		assertEquals(0L, mongoTemplate.count(new Query(), Theme.class))
		
		
		assertEquals(HttpServletResponse.SC_CREATED, response.getStatusLine().getStatusCode())
		assertEquals("application/json;charset=UTF-8", response.getHeaders("Content-Type")[0].getValue())
		
		String responseJson = TestUtil.responseBodyAsString(response)
		Theme responseTheme = new ObjectMapper().readValue(responseJson, Theme.class)
		
		String expectedTheme = """
		{
			"id" : "${responseTheme.getId()}",
			"title" : "Markusevangeliet",
			"description" : "Vi läser igenom markusevangeliet"
		}
		"""
		TestUtil.assertJsonEquals(expectedTheme, responseJson)
		
		assertEquals(1L, mongoTemplate.count(new Query(), Theme.class))
		Theme themeInDatabase = mongoTemplate.findOne(new Query(), Theme.class)
		TestUtil.assertJsonEquals(expectedTheme, new ObjectMapper().writeValueAsString(themeInDatabase))
	}
}
