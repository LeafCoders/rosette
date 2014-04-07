package se.ryttargardskyrkan.rosette.integration.poster.update

import javax.servlet.http.HttpServletResponse
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpPut
import org.junit.Test
import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.integration.util.TestUtil
import se.ryttargardskyrkan.rosette.model.Poster
import com.mongodb.util.JSON

public class UpdatePosterWithEmptyTitleTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {

		// Given
		givenUser(user1)
		givenPermissionForUser(user1, """["update:posters"]""")
		def uploadItem = givenUploadInFolder("posters", validPNGImage)
		givenPoster(poster1, uploadItem)

		// When
		HttpPut putRequest = new HttpPut(baseUrl + "/posters/${poster1['_id']}")
		HttpResponse putResponse = whenPut(putRequest, user1, """
		{
			"title" : "",
			"startTime" : "2014-01-01 11:00 Europe/Stockholm",
			"endTime" : "2014-01-01 18:00 Europe/Stockholm",
			"duration" : 10,
            "image" : { "idRef" : "${uploadItem['id']}" }
		}
		""")

		// Then
		thenResponseCodeIs(putResponse, HttpServletResponse.SC_BAD_REQUEST)
		thenResponseHeaderHas(putResponse, "Content-Type", "application/json;charset=UTF-8")
		
		String responseBody = TestUtil.jsonFromResponse(putResponse)
		String expectedData = """
		[{
			"property" : "title", "message" : "poster.title.notEmpty"
		}]
		"""
		thenResponseDataIs(responseBody, expectedData)
		thenItemsInDatabaseIs(Poster.class, 1)
	}
}
