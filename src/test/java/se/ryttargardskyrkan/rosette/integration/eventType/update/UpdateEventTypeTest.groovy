package se.ryttargardskyrkan.rosette.integration.eventType.update

import com.mongodb.util.JSON
import org.apache.http.HttpResponse
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpPut
import org.apache.http.entity.StringEntity
import org.apache.http.impl.auth.BasicScheme
import org.codehaus.jackson.map.ObjectMapper
import org.junit.Test
import org.springframework.data.mongodb.core.query.Query
import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.integration.util.TestUtil
import se.ryttargardskyrkan.rosette.model.EventType
import se.ryttargardskyrkan.rosette.security.RosettePasswordService

import javax.servlet.http.HttpServletResponse

import static junit.framework.Assert.assertEquals

public class UpdateEventTypeTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given
        String hashedPassword = new RosettePasswordService().encryptPassword("password")
        mongoTemplate.getCollection("users").insert(JSON.parse("""
        [{
            "_id" : "1",
            "username" : "user@host.com",
            "hashedPassword" : "${hashedPassword}",
            "status" : "active"
        }]
        """))

        mongoTemplate.getCollection("permissions").insert(JSON.parse("""
        [{
            "_id" : "1",
            "everyone" : true,
            "patterns" : ["*"]
        }]
        """))

        mongoTemplate.getCollection("eventTypes").insert(JSON.parse("""
  		[{
			"_id" : "1",
			"name" : "Gudstjänst"
		},
		{
			"_id" : "2",
			"name" : "Bön"
  		}]
  		"""))


		// When
		HttpPut putRequest = new HttpPut(baseUrl + "/eventTypes/2")
		String requestBody = """
		{
			"name" : "Alpha"
		}
		"""
		putRequest.setEntity(new StringEntity(requestBody, "application/json", "UTF-8"))
		putRequest.addHeader(new BasicScheme().authenticate(new UsernamePasswordCredentials("user@host.com", "password"), putRequest))
		HttpResponse response = httpClient.execute(putRequest)

		// Then

		// Asserting response
		assertEquals(HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode())

		// Asserting groups in database
		Query queryEventTypes = new Query()
		List<EventType> eventTypesInDatabase = mongoTemplate.find(queryEventTypes, EventType.class)

		assertEquals(2L, mongoTemplate.count(new Query(), EventType.class))
		TestUtil.assertJsonEquals("""
		[{
			"id" : "1",
			"name" : "Gudstjänst"
        },
        {
            "id" : "2",
			"name" : "Alpha"
		}]
		""", new ObjectMapper().writeValueAsString(eventTypesInDatabase))
	}
}
