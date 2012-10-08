package se.ryttargardskyrkan.rosette.integration.event.create

import static org.junit.Assert.*

import javax.servlet.http.HttpServletResponse

import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.codehaus.jackson.map.ObjectMapper
import org.codehaus.jackson.type.TypeReference;
import org.junit.Test
import org.springframework.data.mongodb.core.query.Query

import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.integration.util.TestUtil
import se.ryttargardskyrkan.rosette.model.Event

public class CreateEventWithoutAuthenticationTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given
		
		// When
		HttpPost postRequest = new HttpPost(baseUrl + "/events")
		String requestBody = """
		{
			"title" : "Gudstjänst",
			"startTime" : "2012-03-25 11:00 Europe/Stockholm"
		}
		"""
		postRequest.setEntity(new StringEntity(requestBody, "application/json", "UTF-8"))
		HttpResponse response = httpClient.execute(postRequest)

		// Then
//		Authentication disabled for now
//		assertEquals(HttpServletResponse.SC_FORBIDDEN, response.getStatusLine().getStatusCode())
//		assertEquals("Forbidden", response.getStatusLine().getReasonPhrase())
//		assertEquals(0L, mongoTemplate.count(new Query(), Event.class))
		
		assertEquals(HttpServletResponse.SC_CREATED, response.getStatusLine().getStatusCode())
		assertEquals("application/json;charset=UTF-8", response.getHeaders("Content-Type")[0].getValue())
				
		String responseJson = TestUtil.jsonFromResponse(response)
		Event responseEvent = new ObjectMapper().readValue(responseJson, new TypeReference<Event>() {})
		
		String expectedEvent = """
		{
			"id" : "${responseEvent.getId()}",
			"title" : "Gudstjänst",
			"startTime" : "2012-03-25 11:00 Europe/Stockholm",
			"endTime" : null,
			"description" : null,
			"themeId" : null
		}
		"""
		TestUtil.assertJsonEquals(expectedEvent, responseJson)
		
		assertEquals(1L, mongoTemplate.count(new Query(), Event.class))
		Event eventInDatabase = mongoTemplate.findOne(new Query(), Event.class)
		TestUtil.assertJsonEquals(expectedEvent, new ObjectMapper().writeValueAsString(eventInDatabase))
	}
}
