package se.leafcoders.rosette.integration.upload

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost
import org.junit.Test;
import com.mongodb.util.JSON;
import se.leafcoders.rosette.integration.AbstractIntegrationTest;
import se.leafcoders.rosette.integration.util.TestUtil;

public class CreateUploadTest extends AbstractIntegrationTest {

    @Test
    public void createUploadWithSuccess() throws ClientProtocolException, IOException {
        // Given
		givenUser(user1)
		givenUploadFolder(uploadFolderPosters)
		givenPermissionForUser(user1, ["create:uploads:posters"])

        // When
		String postUrl = "/uploads/posters"
		HttpResponse postResponse = whenPost(postUrl, user1, """{
	        "fileName" : "image.png",
	        "mimeType" : "image/png",
	        "fileData" : "${validPNGImage.fileData}"
		}""")

        // Then
		String responseBody = thenResponseCodeIs(postResponse, HttpServletResponse.SC_CREATED)
		thenResponseHeaderHas(postResponse, "Content-Type", "application/json;charset=UTF-8")

		String expectedData = """{
			"id" : "${ JSON.parse(responseBody)['id'] }",
			"fileName" : "image.png",
            "folderId": "posters",
			"fileUrl" : "${baseUrl}/assets/posters/image.png",
            "mimeType" : "image/png",
			"fileSize" : 1047,
            "width" : 16,
            "height" : 16
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
		givenPermissionForUser(user1, ["create:uploads:invalid"])

		// When
		String postUrl = "/uploads/posters"
		HttpResponse postResponse = whenPost(postUrl, user1, """{
	        "fileName" : "image.png",
	        "mimeType" : "image/png",
	        "fileData" : ${validPNGImage.fileData}
		}""")

		// Then
		thenResponseCodeIs(postResponse, HttpServletResponse.SC_BAD_REQUEST)
	}
}
