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
			"startTime" : ${TestUtil.mongoDate("2012-07-25 11:00 Europe/Stockholm")},
			"endTime" : ${TestUtil.mongoDate("2012-08-26 11:00 Europe/Stockholm")},
			"duration" : 15
  		}]
  		"""))


		// When
		HttpPut putRequest = new HttpPut(baseUrl + "/posters/2")
		String requestBody = """
		{
			"title" : "Summer Poster",
			"startTime" : "2013-01-02 11:00 Europe/Stockholm",
			"endTime" : "2013-03-04 11:00 Europe/Stockholm",
			"duration" : 16
		}
		"""
		putRequest.setEntity(new StringEntity(requestBody, "application/json", "UTF-8"))
		putRequest.addHeader(new BasicScheme().authenticate(new UsernamePasswordCredentials("user@host.com", "password"), putRequest))
		HttpResponse response = httpClient.execute(putRequest)

		// Then

		// Asserting response
		assertEquals(HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode())

		// Asserting groups in database
		Query queryPosters = new Query()
		List<Poster> postersInDatabase = mongoTemplate.find(queryPosters, Poster.class)

		assertEquals(2L, mongoTemplate.count(new Query(), Poster.class))
		TestUtil.assertJsonEquals("""
		[{
			"id" : "1",
			"title" : "Easter Poster",
			"startTime" : "2012-03-25 11:00 Europe/Stockholm",
			"endTime" : "2012-03-26 11:00 Europe/Stockholm",
			"duration" : 15
        },
        {
            "id" : "2",
			"title" : "Summer Poster",
			"startTime" : "2013-01-02 11:00 Europe/Stockholm",
			"endTime" : "2013-03-04 11:00 Europe/Stockholm",
			"duration" : 16
		}]
		""", new ObjectMapper().writeValueAsString(postersInDatabase))
	}
}
