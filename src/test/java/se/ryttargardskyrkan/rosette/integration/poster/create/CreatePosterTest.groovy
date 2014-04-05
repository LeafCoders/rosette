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

public class CreatePosterTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, """["create:posters", "read:uploads:posters"]""")
		def uploadItem = givenUploadInFolder("posters", validPNGImage)
		
		// When
		HttpPost postRequest = new HttpPost(baseUrl + "/posters")
		HttpResponse postResponse = whenPost(postRequest, user1, """
		{
			"title" : "Easter Poster",
			"startTime" : "2012-03-25 11:00 Europe/Stockholm",
			"endTime" : "2012-03-26 11:00 Europe/Stockholm",
			"duration" : 15,
            "image" : { "idRef" : "${uploadItem['id']}" }
		}
		""")

		// Then
		thenResponseCodeIs(postResponse, HttpServletResponse.SC_CREATED)
		thenResponseHeaderHas(postResponse, "Content-Type", "application/json;charset=UTF-8")

		String responseBody = TestUtil.jsonFromResponse(postResponse)
		String expectedData = """
		{
			"id" : "${JSON.parse(responseBody)['id']}",
			"title" : "Easter Poster",
			"startTime" : "2012-03-25 11:00 Europe/Stockholm",
			"endTime" : "2012-03-26 11:00 Europe/Stockholm",
			"duration" : 15,
            "image" : {"idRef":"${uploadItem['id']}", "text":null, "referredObject":null}
		}
		"""

		thenResponseDataIs(responseBody, expectedData)
		thenDataInDatabaseIs(Poster.class, "[${expectedData}]")
		thenItemsInDatabaseIs(Poster.class, 1)
	}
}
