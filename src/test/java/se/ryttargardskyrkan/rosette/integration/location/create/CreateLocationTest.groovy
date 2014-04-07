package se.ryttargardskyrkan.rosette.integration.location.create

import com.mongodb.util.JSON
import org.apache.http.HttpResponse
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.auth.BasicScheme
import org.codehaus.jackson.map.ObjectMapper
import org.codehaus.jackson.type.TypeReference
import org.junit.Test
import org.springframework.data.mongodb.core.query.Query
import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.integration.util.TestUtil
import se.ryttargardskyrkan.rosette.model.Location
import se.ryttargardskyrkan.rosette.security.RosettePasswordService

import javax.servlet.http.HttpServletResponse

import static junit.framework.Assert.assertEquals

public class CreateLocationTest extends AbstractIntegrationTest {

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

        // When
        HttpPost postRequest = new HttpPost(baseUrl + "/locations")
        String requestBody = """
		{
			"name" : "Kyrksalen",
			"description" : "En stor lokal med plats för ca 700 pers."
		}
		"""
        postRequest.setEntity(new StringEntity(requestBody, "application/json", "UTF-8"))
        postRequest.addHeader(new BasicScheme().authenticate(new UsernamePasswordCredentials("user@host.com", "password"), postRequest));
        HttpResponse response = httpClient.execute(postRequest)

        // Then
        assertEquals(HttpServletResponse.SC_CREATED, response.getStatusLine().getStatusCode())
        assertEquals("application/json;charset=UTF-8", response.getHeaders("Content-Type")[0].getValue())

        // Asserting response
        String responseJson = TestUtil.jsonFromResponse(response)
        Location responseLocation = new ObjectMapper().readValue(responseJson, new TypeReference<Location>() {})

        TestUtil.assertJsonEquals("""
		{
			"id" : "${responseLocation.getId()}",
			"name" : "Kyrksalen",
			"description" : "En stor lokal med plats för ca 700 pers.",
			"directionImage": null
		}
		""", responseJson)

        // Asserting database
        List<Location> locationsInDatabase = mongoTemplate.findAll(Location.class)

        assertEquals(1L, mongoTemplate.count(new Query(), Location.class))
        TestUtil.assertJsonEquals("""
		[{
			"id" : "${responseLocation.getId()}",
			"name" : "Kyrksalen",
			"description" : "En stor lokal med plats för ca 700 pers.",
			"directionImage": null
		}]
		""", new ObjectMapper().writeValueAsString(locationsInDatabase))
    }
}
