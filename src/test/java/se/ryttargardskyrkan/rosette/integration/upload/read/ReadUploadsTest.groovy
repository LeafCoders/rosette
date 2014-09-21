package se.ryttargardskyrkan.rosette.integration.upload.read

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue
import javax.servlet.http.HttpServletResponse
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpGet
import org.junit.Test
import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.integration.util.TestUtil
import com.mongodb.util.JSON

public class ReadUploadsTest extends AbstractIntegrationTest {

    @Test
    public void test() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["read:uploads:posters"])
		def uploadItem1 = givenUploadInFolder("posters", validPNGImage)
		def uploadItem2 = givenUploadInFolder("posters", validJPEGImage)

        // When
        getRequest = new HttpGet(baseUrl + "/uploads/posters")
		HttpResponse uploadResponse = whenGet(getRequest, user1)

        // Then
		thenResponseCodeIs(uploadResponse, HttpServletResponse.SC_OK)
		thenResponseHeaderHas(uploadResponse, "Content-Type", "application/json;charset=UTF-8")

		String responseBody = TestUtil.jsonFromResponse(uploadResponse)
		Object responseObject = JSON.parse(responseBody);
		String expectedData = """[{
			"id" : "${responseObject[0]['id']}",
			"fileName" : "image.png",
            "folderName": "posters",
			"fileUrl" : "${baseUrl}/assets/posters/image.png",
            "mimeType" : "image/png",
			"fileSize" : 1047,
            "width" : 16,
            "height" : 16
		},
		{
			"id" : "${responseObject[1]['id']}",
			"fileName" : "image.jpg",
            "folderName": "posters",
			"fileUrl" : "${baseUrl}/assets/posters/image.jpg",
            "mimeType" : "image/jpg",
			"fileSize" : 1476,
            "width" : 16,
            "height" : 16
		}]"""
		thenResponseDataIs(responseBody, expectedData)
		getRequest.releaseConnection()
    }
}
