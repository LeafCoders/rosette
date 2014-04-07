package se.ryttargardskyrkan.rosette.integration.location.update

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
import se.ryttargardskyrkan.rosette.model.Location
import se.ryttargardskyrkan.rosette.security.RosettePasswordService

import javax.servlet.http.HttpServletResponse

import static junit.framework.Assert.assertEquals

public class UpdateLocationTest extends AbstractIntegrationTest {

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
		HttpPut putRequest = new HttpPut(baseUrl + "/locations/2")
		String requestBody = """
		{
			"name" : "Ekkällan",
			"description" : "Konferensrum för ca 100 pers."
		}
		"""
		putRequest.setEntity(new StringEntity(requestBody, "application/json", "UTF-8"))
		putRequest.addHeader(new BasicScheme().authenticate(new UsernamePasswordCredentials("user@host.com", "password"), putRequest))
		HttpResponse response = httpClient.execute(putRequest)

		// Then

		// Asserting response
		assertEquals(HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode())

		// Asserting groups in database
		Query queryLocations = new Query()
		List<Location> locationsInDatabase = mongoTemplate.find(queryLocations, Location.class)

		assertEquals(2L, mongoTemplate.count(new Query(), Location.class))
		TestUtil.assertJsonEquals("""
		[{
			"id" : "1",
            "name" : "Kyrksalen",
			"description" : "En stor lokal med plats för ca 700 pers.",
			"directionImage": null
		},
		{
			"id" : "2",
            "name" : "Ekkällan",
			"description" : "Konferensrum för ca 100 pers.",
			"directionImage": null
		}]
		""", new ObjectMapper().writeValueAsString(locationsInDatabase))
	}
}
