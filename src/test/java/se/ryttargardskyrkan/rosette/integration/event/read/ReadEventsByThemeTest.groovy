package se.ryttargardskyrkan.rosette.integration.event.read

import static junit.framework.Assert.*

import javax.servlet.http.HttpServletResponse

import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpGet
import org.junit.Test

import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.integration.util.TestUtil

import com.mongodb.util.JSON

public class ReadEventsByThemeTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given
		mongoTemplate.getCollection("themes").insert(JSON.parse("""
		[{
			"_id" : 1,
			"title" : "Markusevangeliet",
			"description" : "Vi läser igenom markusevangeliet"
		},
		{
			"_id" : 2,
			"title" : "Johannesevangeliet",
			"description" : "Vi läser igenom johannesevangeliet"
		}]
		"""))
						
		mongoTemplate.getCollection("events").insert(JSON.parse("""
		[{
			"_id" : "1",
			"title" : "Gudstjänst 1",
			"themeId" : "1"
		},
		{
			"_id" : "2",
			"title" : "Gudstjänst 2",
			"themeId" : "2"
		},
		{
			"_id" : "3",
			"title" : "Gudstjänst 3",
			"themeId" : "1"
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
		HttpGet getRequest = new HttpGet(baseUrl + "/events?themeId=1")
		getRequest.setHeader("Accept", "application/json; charset=UTF-8")
		getRequest.setHeader("Content-Type", "application/json; charset=UTF-8")
		HttpResponse response = httpClient.execute(getRequest)

		// Then
		assertEquals(HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode())
		assertEquals("application/json;charset=UTF-8", response.getHeaders("Content-Type")[0].getValue())
		String expectedEvents = """
		[{
			"id" : "1",
			"title" : "Gudstjänst 1",
			"startTime" : null,
			"endTime" : null,
			"description" : null,
			"themeId" : "1"
		},
		{
			"id" : "3",
			"title" : "Gudstjänst 3",
			"startTime" : null,
			"endTime" : null,
			"description" : null,
			"themeId" : "1"
		}]
		"""
		TestUtil.assertJsonResponseEquals(expectedEvents, response)
	}
}
