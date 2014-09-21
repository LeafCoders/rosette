package se.ryttargardskyrkan.rosette.integration.upload.create

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost
import org.junit.Test;
import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest;

public class CreateUploadWithWrongPermissionTest extends AbstractIntegrationTest {

    @Test
    public void test() throws ClientProtocolException, IOException {
        // Given
		givenUser(user1)
		givenPermissionForUser(user1, ["create:uploads:invalid"])

        // When
        postRequest = new HttpPost(baseUrl + "/uploads/posters")
		HttpResponse postResponse = whenPost(postRequest, user1, validPNGImage)
		
        // Then
		thenResponseCodeIs(postResponse, HttpServletResponse.SC_FORBIDDEN)
    }
}
