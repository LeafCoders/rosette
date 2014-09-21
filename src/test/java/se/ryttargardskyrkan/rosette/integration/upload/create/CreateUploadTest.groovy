package se.ryttargardskyrkan.rosette.integration.upload.create

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost
import org.junit.Test;
import com.mongodb.util.JSON;
import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest;
import se.ryttargardskyrkan.rosette.integration.util.TestUtil;

public class CreateUploadTest extends AbstractIntegrationTest {

    @Test
    public void test() throws ClientProtocolException, IOException {
        // Given
		givenUser(user1)
		givenPermissionForUser(user1, ["create:uploads:posters"])

        // When
        postRequest = new HttpPost(baseUrl + "/uploads/posters")
		HttpResponse postResponse = whenPost(postRequest, user1, validPNGImage)

        // Then
		thenResponseCodeIs(postResponse, HttpServletResponse.SC_CREATED)
		thenResponseHeaderHas(postResponse, "Content-Type", "application/json;charset=UTF-8")

		String responseBody = TestUtil.jsonFromResponse(postResponse)
		String expectedData = """{
			"id" : "${JSON.parse(responseBody)['id']}",
			"fileName" : "image.png",
            "folderName": "posters",
			"fileUrl" : "${baseUrl}/assets/posters/image.png",
            "mimeType" : "image/png",
			"fileSize" : 1047,
            "width" : 16,
            "height" : 16
		}"""
		thenResponseDataIs(responseBody, expectedData)
		postRequest.releaseConnection()
		thenAssetWithNameExist("${JSON.parse(responseBody)['fileName']}", "${JSON.parse(responseBody)['fileUrl']}")
    }
}
