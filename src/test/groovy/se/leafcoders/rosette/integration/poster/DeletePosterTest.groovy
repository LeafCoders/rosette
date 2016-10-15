package se.leafcoders.rosette.integration.poster

import javax.servlet.http.HttpServletResponse
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpDelete
import org.junit.Test
import se.leafcoders.rosette.integration.AbstractIntegrationTest
import se.leafcoders.rosette.integration.util.TestUtil
import se.leafcoders.rosette.model.Poster
import se.leafcoders.rosette.model.upload.UploadFile;
import com.mongodb.util.JSON

public class DeletePosterTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {

		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["posters:delete"])
		givenUploadFolder(uploadFolderPosters)
		UploadFile uploadItem = givenUploadInFolder("posters", validPNGImage)
		givenPoster(poster1, uploadItem)
		givenPoster(poster2, uploadItem)

		// When
		String deleteUrl = "/posters/${ poster1.id }"
		HttpResponse deleteResponse = whenDelete(deleteUrl, user1)

		// Then
		thenResponseCodeIs(deleteResponse, HttpServletResponse.SC_OK)
		thenItemsInDatabaseIs(Poster.class, 1)
	}
}
