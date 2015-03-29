package se.leafcoders.rosette.integration.upload

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue
import javax.servlet.http.HttpServletResponse
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpDelete
import org.junit.Test
import se.leafcoders.rosette.integration.AbstractIntegrationTest

public class DeleteUploadTest extends AbstractIntegrationTest {

    @Test
    public void successToDeleteUpload() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenUploadFolder(uploadFolderPosters)
		givenPermissionForUser(user1, ["delete:uploads:posters"])
		def uploadItem = givenUploadInFolder("posters", validPNGImage)

        // When
		String deleteUrl = "/uploads/posters/${uploadItem['id']}"
		HttpResponse uploadResponse = whenDelete(deleteUrl, user1)
		releaseDeleteRequest()
		
        // Then
		thenResponseCodeIs(uploadResponse, HttpServletResponse.SC_OK)
    }

	@Test
	public void failToDeleteWhenUploadIsReferenced() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenUploadFolder(uploadFolderPosters)
		givenPermissionForUser(user1, ["delete:uploads:posters"])
		def uploadItem = givenUploadInFolder("posters", validPNGImage)
		givenPoster(poster1, uploadItem)
		
		// When
		String deleteUrl = "/uploads/posters/${uploadItem['id']}"
		HttpResponse uploadResponse = whenDelete(deleteUrl, user1)

		// Then
		thenResponseCodeIs(uploadResponse, HttpServletResponse.SC_FORBIDDEN)
		releaseDeleteRequest()
		thenAssetWithNameExist(uploadItem['fileName'], uploadItem['fileUrl'])
	}

}
