package se.ryttargardskyrkan.rosette.integration.poster.delete

import javax.servlet.http.HttpServletResponse
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpDelete
import org.junit.Test
import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.integration.util.TestUtil
import se.ryttargardskyrkan.rosette.model.Poster
import com.mongodb.util.JSON

public class DeletePosterTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {

		// Given
		givenUser(user1)
		givenPermissionForUser(user1, """["delete:posters"]""")
		def uploadItem = givenUploadInFolder("posters", validPNGImage)
		givenPoster(poster1, uploadItem)
		givenPoster(poster2, uploadItem)

		// When
		HttpDelete deleteRequest = new HttpDelete(baseUrl + "/posters/${poster1['_id']}")
		HttpResponse deleteResponse = whenDelete(deleteRequest, user1)

		// Then
		thenResponseCodeIs(deleteResponse, HttpServletResponse.SC_OK)
		thenItemsInDatabaseIs(Poster.class, 1)
	}
}
