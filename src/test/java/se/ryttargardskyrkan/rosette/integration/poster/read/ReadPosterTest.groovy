package se.ryttargardskyrkan.rosette.integration.poster.read

import com.mongodb.util.JSON
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpGet
import org.junit.Test
import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.integration.util.TestUtil

import javax.servlet.http.HttpServletResponse

import static junit.framework.Assert.assertEquals

public class ReadPosterTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given
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
		
		mongoTemplate.getCollection("permissions").insert(JSON.parse("""
		[{
			"_id" : "1",
			"everyone" : true,
			"patterns" : ["*"]
		}]
		"""));

		// When
		HttpGet getRequest = new HttpGet(baseUrl + "/posters/2")
		getRequest.setHeader("Accept", "application/json; charset=UTF-8")
		getRequest.setHeader("Content-Type", "application/json; charset=UTF-8")
		HttpResponse response = httpClient.execute(getRequest)

		// Then
		assertEquals(HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode())
		assertEquals("application/json;charset=UTF-8", response.getHeaders("Content-Type")[0].getValue())
		String expectedPoster = """
		{
			"id" : "2",
			"title" : "Christmas Eve",
			"startTime" : "2012-07-25 11:00 Europe/Stockholm",
			"endTime" : "2012-08-26 11:00 Europe/Stockholm",
			"duration" : 15
		}
		"""
		TestUtil.assertJsonResponseEquals(expectedPoster, response)
	}
}
