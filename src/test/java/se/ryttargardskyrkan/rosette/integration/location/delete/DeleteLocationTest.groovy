package se.ryttargardskyrkan.rosette.integration.location.delete

import com.mongodb.util.JSON
import org.apache.http.HttpResponse
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpDelete
import org.apache.http.impl.auth.BasicScheme
import org.codehaus.jackson.map.ObjectMapper
import org.junit.Test
import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.integration.util.TestUtil
import se.ryttargardskyrkan.rosette.model.Location
import se.ryttargardskyrkan.rosette.security.RosettePasswordService

import javax.servlet.http.HttpServletResponse

import static junit.framework.Assert.assertEquals

public class DeleteLocationTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given
		String hashedPassword = new RosettePasswordService().encryptPassword("password");
		mongoTemplate.getCollection("users").insert(JSON.parse("""
		[{
			"_id" : "1",
			"username" : "user@host.com",
			"hashedPassword" : "${hashedPassword}",
			"status" : "active"
		}]
		"""));
	
		mongoTemplate.getCollection("permissions").insert(JSON.parse("""
		[{
			"_id" : "1",
			"userId" : "1",
			"patterns" : ["*"]
		}]
		"""));

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

		// When
		HttpDelete deleteRequest = new HttpDelete(baseUrl + "/locations/2")
		deleteRequest.setHeader("Accept", "application/json; charset=UTF-8")
		deleteRequest.setHeader("Content-Type", "application/json; charset=UTF-8")
		deleteRequest.addHeader(new BasicScheme().authenticate(new UsernamePasswordCredentials("user@host.com", "password"), deleteRequest));
		HttpResponse response = httpClient.execute(deleteRequest)

		// Then

		// Asserting response
		assertEquals(HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode())

		// Asserting groups in database
		List<Location> locationsInDatabase = mongoTemplate.findAll(Location.class)
		TestUtil.assertJsonEquals("""
		[{
			"id" : "1",
			"name" : "Kyrksalen",
			"description" : "En stor lokal med plats för ca 700 pers."
		}]""", new ObjectMapper().writeValueAsString(locationsInDatabase))
	}
}
