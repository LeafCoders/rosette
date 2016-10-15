package se.leafcoders.rosette.integration.upload

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue
import javax.servlet.http.HttpServletResponse
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpGet
import org.junit.Test
import se.leafcoders.rosette.integration.AbstractIntegrationTest
import se.leafcoders.rosette.integration.util.TestUtil
import com.mongodb.util.JSON

public class ReadUploadsTest extends AbstractIntegrationTest {

    @Test
    public void test() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenUploadFolder(uploadFolderPosters)
		givenPermissionForUser(user1, ["uploads:read:posters"])
		def uploadItem1 = givenUploadInFolder("posters", validPNGImage)
		def uploadItem2 = givenUploadInFolder("posters", validJPEGImage)

        // When
		String getUrl = "/uploads/posters"
		HttpResponse uploadFile = whenGet(getUrl, user1)

        // Then
		String responseBody = thenResponseCodeIs(uploadFile, HttpServletResponse.SC_OK)
		thenResponseHeaderHas(uploadFile, "Content-Type", "application/json;charset=UTF-8")

		String expectedData = """[
            {
                "id" : "${ uploadItem1['id'] }",
                "fileName" : "image.png",
                "folderId": "posters",
                "fileUrl" : "${ baseApiUrl }/assets/posters/image.png",
                "mimeType" : "image/png",
                "fileSize" : 1741,
                "width" : 500,
                "height" : 400
            },
			{
                "id" : "${ uploadItem2['id'] }",
                "fileName" : "image.jpg",
                "folderId": "posters",
                "fileUrl" : "${ baseApiUrl }/assets/posters/image.jpg",
                "mimeType" : "image/jpg",
                "fileSize" : 7635,
                "width" : 500,
                "height" : 400
			}
		]"""
		thenResponseDataIs(responseBody, expectedData)
    }
}
