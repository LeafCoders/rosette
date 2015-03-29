package se.leafcoders.rosette.integration.poster

import javax.servlet.http.HttpServletResponse
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpGet
import org.junit.Test
import se.leafcoders.rosette.integration.AbstractIntegrationTest
import se.leafcoders.rosette.integration.util.TestUtil
import se.leafcoders.rosette.model.Poster
import se.leafcoders.rosette.model.upload.UploadResponse;
import com.mongodb.util.JSON

public class ReadPostersTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {

		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["read:posters"])
		givenUploadFolder(uploadFolderPosters)
		UploadResponse uploadItem = givenUploadInFolder("posters", validPNGImage)
		givenPoster(poster1, uploadItem)
		givenPoster(poster2, uploadItem)

		// When
		String getUrl = "/posters/"
		HttpResponse getResponse = whenGet(getUrl, user1)

		// Then
		String responseBody = thenResponseCodeIs(getResponse, HttpServletResponse.SC_OK)
		thenResponseHeaderHas(getResponse, "Content-Type", "application/json;charset=UTF-8")

		String expectedData = """[
			{
				"id" : "${ poster2.id }",
				"title" : "Poster2 title",
				"startTime" : "2012-03-26 10:00 Europe/Stockholm",
				"endTime" : "2012-03-27 18:00 Europe/Stockholm",
				"duration" : 10,
				"image" : ${ toJSON(uploadItem) }
			},
			{
				"id" : "${ poster1.id }",
				"title" : "Poster1 title",
				"startTime" : "2012-03-25 11:00 Europe/Stockholm",
				"endTime" : "2012-03-26 11:00 Europe/Stockholm",
				"duration" : 15,
				"image" : ${ toJSON(uploadItem) }
			}
		]"""
		thenResponseDataIs(responseBody, expectedData)
	}
}
