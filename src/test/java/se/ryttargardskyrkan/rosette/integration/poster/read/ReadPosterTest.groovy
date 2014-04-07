package se.ryttargardskyrkan.rosette.integration.poster.read

import javax.servlet.http.HttpServletResponse
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpGet
import org.junit.Test
import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.integration.util.TestUtil
import se.ryttargardskyrkan.rosette.model.Poster
import com.mongodb.util.JSON

public class ReadPosterTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {

		// Given
		givenUser(user1)
		givenPermissionForUser(user1, """["read:posters", "read:uploads:posters"]""")
		def uploadItem = givenUploadInFolder("posters", validPNGImage)
		givenPoster(poster1, uploadItem)
		givenPoster(poster2, uploadItem)
		
		// When
		HttpGet getRequest = new HttpGet(baseUrl + "/posters/${poster1['_id']}")
		HttpResponse getResponse = whenGet(getRequest, user1)

		// Then
		thenResponseCodeIs(getResponse, HttpServletResponse.SC_OK)
		thenResponseHeaderHas(getResponse, "Content-Type", "application/json;charset=UTF-8")

		String responseBody = TestUtil.jsonFromResponse(getResponse)
		String expectedData = """
		{
			"id" : "${JSON.parse(responseBody)['id']}",
			"title" : "Poster1 title",
			"startTime" : "2012-03-25 11:00 Europe/Stockholm",
			"endTime" : "2012-03-26 11:00 Europe/Stockholm",
			"duration" : 15,
			"image" : { "idRef" : "${uploadItem['id']}", "text": null, "referredObject": {
				"id" : "${uploadItem['id']}",
				"fileName" : "image.png",
				"folder" : "posters",
				"fileUrl" : "http://localhost:9000/api/v1-snapshot/assets/posters/image.png",
				"mimeType" : "image/png",
				"fileSize" : 1047,
				"width" : 16,
				"height" : 16
			}}
		}
		"""
		thenResponseDataIs(responseBody, expectedData)
		thenItemsInDatabaseIs(Poster.class, 2)
	}
}
