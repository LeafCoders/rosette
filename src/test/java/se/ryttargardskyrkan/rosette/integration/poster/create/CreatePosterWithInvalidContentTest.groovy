package se.ryttargardskyrkan.rosette.integration.poster.create

import javax.servlet.http.HttpServletResponse
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpPost
import org.junit.Test
import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.integration.util.TestUtil
import se.ryttargardskyrkan.rosette.model.Poster
import com.mongodb.util.JSON

public class CreatePosterWithInvalidContentTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, """["create:posters"]""")

		// When
		HttpPost postRequest = new HttpPost(baseUrl + "/posters")
		HttpResponse postResponse = whenPost(postRequest, user1, """
		{
			"title" : "",
			"startTime" : "2012-03-25 11:00 Europe/Stockholm",
			"endTime" : "2011-02-26 10:00 Europe/Stockholm",
			"duration" : 0
		}
		""")

		// Then
		thenResponseCodeIs(postResponse, HttpServletResponse.SC_BAD_REQUEST)
		thenResponseHeaderHas(postResponse, "Content-Type", "application/json;charset=UTF-8")

		String responseBody = TestUtil.jsonFromResponse(postResponse)
		String expectedData = """
		[
			{ "property" : "duration", "message" : "poster.duration.tooShort" },
			{ "property" : "image",    "message" : "poster.image.mustBeSet" },
			{ "property" : "",         "message" : "poster.startBeforeEndTime" },
			{ "property" : "title",    "message" : "poster.title.notEmpty" }
		]
		"""
		
		thenResponseDataIs(responseBody, expectedData)
		thenItemsInDatabaseIs(Poster.class, 0)
	}
}
