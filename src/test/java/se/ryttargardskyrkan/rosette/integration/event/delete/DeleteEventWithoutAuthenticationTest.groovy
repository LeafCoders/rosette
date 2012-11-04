package se.ryttargardskyrkan.rosette.integration.event.delete;

import static junit.framework.Assert.*

import javax.servlet.http.HttpServletResponse

import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpDelete
import org.junit.Test
import org.springframework.data.mongodb.core.query.Query

import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.model.Event

import com.mongodb.util.JSON

public class DeleteEventWithoutAuthenticationTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given		
		mongoTemplate.getCollection("events").insert(JSON.parse("""
		[{
			"_id" : "1",
			"title" : "Gudstjänst 1"
		},
		{
			"_id" : "2",
			"title" : "Gudstjänst 2"
		}]
		"""));

		// When
		HttpDelete deleteRequest = new HttpDelete(baseUrl + "/events/1")
		deleteRequest.setHeader("Accept", "application/json; charset=UTF-8")
		deleteRequest.setHeader("Content-Type", "application/json; charset=UTF-8")
		HttpResponse response = httpClient.execute(deleteRequest)

		// Then
		assertEquals(HttpServletResponse.SC_FORBIDDEN, response.getStatusLine().getStatusCode())
		assertEquals("Forbidden", response.getStatusLine().getReasonPhrase())
		assertEquals(2L, mongoTemplate.count(new Query(), Event.class))
	}
}
