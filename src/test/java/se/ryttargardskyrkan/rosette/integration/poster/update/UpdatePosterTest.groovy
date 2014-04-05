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

public class UpdatePosterTest extends AbstractIntegrationTest {

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
			"title" : "New title",
			"startTime" : "2014-01-01 11:00 Europe/Stockholm",
			"endTime" : "2014-01-01 18:00 Europe/Stockholm",
			"duration" : 10,
            "image" : { "idRef" : "${uploadItem['id']}" }
		}
		""")

		// Then
		thenResponseCodeIs(putResponse, HttpServletResponse.SC_OK)

		String expectedData = """
		{
			"id" : "${poster1['_id']}",
			"title" : "New title",
			"startTime" : "2014-01-01 11:00 Europe/Stockholm",
			"endTime" : "2014-01-01 18:00 Europe/Stockholm",
			"duration" : 10,
			"image" : { "idRef" : "${uploadItem['id']}", "text" : null, "referredObject" : null }
		}
		"""

		thenDataInDatabaseIs(Poster.class, "[${expectedData}]")
		thenItemsInDatabaseIs(Poster.class, 1)
	}
}
