package se.ryttargardskyrkan.rosette.integration.poster.update

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
import se.ryttargardskyrkan.rosette.model.Poster

import javax.servlet.http.HttpServletResponse

import static junit.framework.Assert.assertEquals
import se.ryttargardskyrkan.rosette.security.RosettePasswordService

public class UpdatePosterTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given
        String hashedPassword = new RosettePasswordService().encryptPassword("password");
        mongoTemplate.getCollection("users").insert(JSON.parse("""
        [{
            "_id" : "1",
            "username" : "lars.arvidsson@gmail.com",
            "hashedPassword" : "${hashedPassword}",
            "status" : "active"
        }]
        """));

        mongoTemplate.getCollection("permissions").insert(JSON.parse("""
        [{
            "_id" : "1",
            "everyone" : true,
            "patterns" : ["*"]
        }]
        """));

        mongoTemplate.getCollection("posters").insert(JSON.parse("""
      		[{
      			"_id" : "1",
      			"title" : "Easter Poster",
      			"imageName" : "easter.jpg"
      		},
      		{
      			"_id" : "2",
      			"title" : "Christmas Eve",
      			"imageName" : "santa.jpg"
      		}]
      		"""))



		// When
		HttpPut putRequest = new HttpPut(baseUrl + "/posters/2")
		String requestBody = """
		{
			"title" : "Summer Poster",
			"imageName" : "summer.jpg"
		}
		"""
		putRequest.setEntity(new StringEntity(requestBody, "application/json", "UTF-8"))
		putRequest.addHeader(new BasicScheme().authenticate(new UsernamePasswordCredentials("lars.arvidsson@gmail.com", "password"), putRequest));
		HttpResponse response = httpClient.execute(putRequest)

		// Then

		// Asserting response
		assertEquals(HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode())

		// Asserting groups in database
		Query queryPosters = new Query();
		List<Poster> postersInDatabase = mongoTemplate.find(queryPosters, Poster.class);

		assertEquals(2L, mongoTemplate.count(new Query(), Poster.class))
		TestUtil.assertJsonEquals("""
		[{
			"id" : "1",
			"title" : "Easter Poster",
			"imageName" : "easter.jpg"
		},{
			"id" : "2",
			"title" : "Summer Poster",
			"imageName" : "summer.jpg"
		}]
		""", new ObjectMapper().writeValueAsString(postersInDatabase))
	}
}
