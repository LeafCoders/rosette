package se.ryttargardskyrkan.rosette.integration.poster

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
	public void readPosterWithSuccess() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["read:posters", "read:uploads:posters"])
		def uploadItem = givenUploadInFolder("posters", validPNGImage)
		givenPoster(poster1, uploadItem)
		givenPoster(poster2, uploadItem)
		
		// When
		String getUrl = "/posters/${ poster1.id }"
		HttpResponse getResponse = whenGet(getUrl, user1)

		// Then
		String responseBody = thenResponseCodeIs(getResponse, HttpServletResponse.SC_OK)
		thenResponseHeaderHas(getResponse, "Content-Type", "application/json;charset=UTF-8")

		String expectedData = """{
			"id" : "${ poster1.id }",
			"title" : "Poster1 title",
			"startTime" : "2012-03-25 11:00 Europe/Stockholm",
			"endTime" : "2012-03-26 11:00 Europe/Stockholm",
			"duration" : 15,
			"image" : {
				"idRef" : "${ uploadItem['id'] }",
				"referredObject" : {
					"id" : "${ uploadItem['id'] }",
					"fileName" : "image.png",
					"folderName" : "posters",
					"fileUrl" : "http://localhost:9000/api/v1-snapshot/assets/posters/image.png",
					"mimeType" : "image/png",
					"fileSize" : 1047,
					"width" : 16,
					"height" : 16
				}
			}
		}"""
		thenResponseDataIs(responseBody, expectedData)
		thenItemsInDatabaseIs(Poster.class, 2)
	}
}
