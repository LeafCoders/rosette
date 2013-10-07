package se.ryttargardskyrkan.rosette.integration.poster.delete

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
import se.ryttargardskyrkan.rosette.model.Poster
import se.ryttargardskyrkan.rosette.security.RosettePasswordService

import javax.servlet.http.HttpServletResponse

import static junit.framework.Assert.assertEquals

public class DeletePosterTest extends AbstractIntegrationTest {

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

        mongoTemplate.getCollection("posters").insert(JSON.parse("""
        [{
            "_id" : "1",
            "title" : "Easter Poster",
			"startTime" : ${TestUtil.mongoDate("2012-03-25 11:00 Europe/Stockholm")},
			"endTime" : ${TestUtil.mongoDate("2012-03-26 11:00 Europe/Stockholm")},
			"duration" : 15
        },
        {
            "_id" : "2",
            "title" : "Christmas Eve",
			"startTime" : ${TestUtil.mongoDate("2012-03-25 11:01 Europe/Stockholm")},
			"endTime" : ${TestUtil.mongoDate("2012-03-26 11:01 Europe/Stockholm")},
			"duration" : 15
        }]
        """))

		// When
		HttpDelete deleteRequest = new HttpDelete(baseUrl + "/posters/2")
		deleteRequest.setHeader("Accept", "application/json; charset=UTF-8")
		deleteRequest.setHeader("Content-Type", "application/json; charset=UTF-8")
		deleteRequest.addHeader(new BasicScheme().authenticate(new UsernamePasswordCredentials("user@host.com", "password"), deleteRequest));
		HttpResponse response = httpClient.execute(deleteRequest)

		// Then

		// Asserting response
		assertEquals(HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode())

		// Asserting groups in database
		List<Poster> postersInDatabase = mongoTemplate.findAll(Poster.class)
		TestUtil.assertJsonEquals("""
		[{
			"id" : "1",
			"title" : "Easter Poster",
			"startTime" : "2012-03-25 11:00 Europe/Stockholm",
			"endTime" : "2012-03-26 11:00 Europe/Stockholm",
			"duration" : 15
		}]""", new ObjectMapper().writeValueAsString(postersInDatabase))
	}
}
