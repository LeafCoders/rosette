package se.ryttargardskyrkan.rosette.integration.upload.delete

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue
import javax.servlet.http.HttpServletResponse
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpDelete
import org.junit.Test
import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest

public class DeleteUploadTest extends AbstractIntegrationTest {

    @Test
    public void test() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["delete:uploads:posters"])
		def uploadItem = givenUploadInFolder("posters", validPNGImage)

        // When
        deleteRequest = new HttpDelete(baseUrl + "/uploads/posters/${uploadItem['id']}")
		HttpResponse uploadResponse = whenDelete(deleteRequest, user1)
		deleteRequest.releaseConnection()

        // Then
		thenResponseCodeIs(uploadResponse, HttpServletResponse.SC_OK)
		thenAssetDontExist(uploadItem['fileUrl'])
    }
}
