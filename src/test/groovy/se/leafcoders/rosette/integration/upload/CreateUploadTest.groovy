package se.leafcoders.rosette.integration.upload

import javax.servlet.http.HttpServletResponse
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.junit.Test
import se.leafcoders.rosette.integration.AbstractIntegrationTest
import se.leafcoders.rosette.model.upload.UploadRequest
import com.mongodb.util.JSON

public class CreateUploadTest extends AbstractIntegrationTest {

    @Test
    public void createUploadWithSuccess() throws ClientProtocolException, IOException {
        // Given
		givenUser(user1)
		givenUploadFolder(uploadFolderPosters)
		givenPermissionForUser(user1, ["uploads:create:posters"])

        // When
		String postUrl = "/uploads/posters"
        HttpResponse postResponse = whenPostUpload(postUrl, user1, new UploadRequest(
            fileName: "image.png",
            mimeType: "image/png"
        ))

        // Then
		String responseBody = thenResponseCodeIs(postResponse, HttpServletResponse.SC_CREATED)
		thenResponseHeaderHas(postResponse, "Content-Type", "application/json;charset=UTF-8")

		String expectedData = """{
			"id" : "${ JSON.parse(responseBody)['id'] }",
			"fileName" : "image.png",
            "folderId": "posters",
			"fileUrl" : "${baseApiUrl}/assets/posters/image.png",
            "mimeType" : "image/png",
            "fileSize" : 1741,
            "width" : 500,
            "height" : 400
		}"""
		thenResponseDataIs(responseBody, expectedData)
		releasePostRequest();
		thenAssetWithNameExist("${JSON.parse(responseBody)['fileName']}", "${JSON.parse(responseBody)['fileUrl']}")
    }

	@Test
	public void failWhenUploadFileWithoutPermission() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenUploadFolder(uploadFolderPosters)
		givenPermissionForUser(user1, ["uploads:create:invalid"])

		// When
		String postUrl = "/uploads/posters"
		HttpResponse postResponse = whenPostUpload(postUrl, user1, new UploadRequest(
	        fileName: "image.png",
	        mimeType: "image/png"
		))

		// Then
        String responseBody = thenResponseCodeIs(postResponse, HttpServletResponse.SC_FORBIDDEN)
        thenResponseDataIs(responseBody, """{
            "error" : "error.forbidden",
            "reason" : "error.missingPermission",
            "reasonParams" : ["uploads:create:posters"]
        }""")
	}
}
