package se.ryttargardskyrkan.rosette.integration.poster

import java.io.IOException;
import javax.servlet.http.HttpServletResponse
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpPut
import org.junit.Test
import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.integration.util.TestUtil
import se.ryttargardskyrkan.rosette.model.Poster
import se.ryttargardskyrkan.rosette.model.UploadResponse
import com.mongodb.util.JSON

public class UpdatePosterTest extends AbstractIntegrationTest {

	@Test
	public void updatePosterWithSuccess() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["update:posters", "read:posters", "read:uploads"])
		UploadResponse uploadItem = givenUploadInFolder("posters", validPNGImage)
		givenPoster(poster1, uploadItem)

		// When
		String putUrl = "/posters/${ poster1.id }"
		HttpResponse putResponse = whenPut(putUrl, user1, """{
			"title" : "New title",
			"startTime" : "2014-01-01 11:00 Europe/Stockholm",
			"endTime" : "2014-01-01 18:00 Europe/Stockholm",
			"duration" : 10,
            "image" : ${ toJSON(uploadItem) }
		}""")

		// Then
		thenResponseCodeIs(putResponse, HttpServletResponse.SC_OK)

		String expectedData = """[{
			"id" : "${ poster1.id }",
			"title" : "New title",
			"startTime" : "2014-01-01 11:00 Europe/Stockholm",
			"endTime" : "2014-01-01 18:00 Europe/Stockholm",
			"duration" : 10,
			"image" : ${ toJSON(uploadItem) }
		}]"""
		thenDataInDatabaseIs(Poster.class, expectedData)
		thenItemsInDatabaseIs(Poster.class, 1)
	}

	@Test
	public void failWhenUpdatePosterWithEmptyTitle() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["update:posters", "read:posters", "read:uploads"])
		def uploadItem = givenUploadInFolder("posters", validPNGImage)
		givenPoster(poster1, uploadItem)

		// When
		String putUrl = "/posters/${ poster1.id }"
		HttpResponse putResponse = whenPut(putUrl, user1, """{
			"title" : "",
			"startTime" : "2014-01-01 11:00 Europe/Stockholm",
			"endTime" : "2014-01-01 18:00 Europe/Stockholm",
			"duration" : 10,
			"image" : ${ toJSON(uploadItem) }
		}""")

		// Then
		String responseBody = thenResponseCodeIs(putResponse, HttpServletResponse.SC_BAD_REQUEST)
		thenResponseHeaderHas(putResponse, "Content-Type", "application/json;charset=UTF-8")
		
		String expectedData = """[
			{ "property" : "title", "message" : "poster.title.notEmpty" }
		]"""
		thenResponseDataIs(responseBody, expectedData)
		thenItemsInDatabaseIs(Poster.class, 1)
	}
}
