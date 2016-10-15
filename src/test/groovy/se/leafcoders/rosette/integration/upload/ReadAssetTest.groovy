package se.leafcoders.rosette.integration.upload

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue
import javax.servlet.http.HttpServletResponse
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.junit.Test
import se.leafcoders.rosette.integration.AbstractIntegrationTest
import se.leafcoders.rosette.model.upload.UploadFile

public class ReadAssetTest extends AbstractIntegrationTest {

    @Test
    public void readAssetById() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenUploadFolder(uploadFolderPosters)
		givenPermissionForUser(user1, ["uploads:read:posters"])
		UploadFile uploadItem = givenUploadInFolder("posters", validJPEGImage)

        // When
		String getUrl = "/assets/posters/" + uploadItem.id
		HttpResponse asset = whenGetFile(getUrl, user1)

        // Then
		String responseBody = thenResponseCodeIs(asset, HttpServletResponse.SC_OK)
		thenResponseHeaderHas(asset, "Content-Type", "image/jpg")
    }

    @Test
    public void readThumbAssetById() throws ClientProtocolException, IOException {
        // Given
        givenUser(user1)
        givenUploadFolder(uploadFolderPosters)
        givenPermissionForUser(user1, ["uploads:read:posters"])
        UploadFile uploadItem = givenUploadInFolder("posters", validJPEGImage)

        // When
        String getUrl = "/assets/posters/icon/" + uploadItem.id
        HttpResponse asset = whenGetFile(getUrl, user1)

        // Then
        String responseBody = thenResponseCodeIs(asset, HttpServletResponse.SC_OK)
        thenResponseHeaderHas(asset, "Content-Type", "image/jpg")
    }

    @Test
    public void readAssetByFileName() throws ClientProtocolException, IOException {
        // Given
        givenUser(user1)
        givenUploadFolder(uploadFolderPosters)
        givenPermissionForUser(user1, ["uploads:read:posters"])
        UploadFile uploadItem = givenUploadInFolder("posters", validPNGImage)

        // When
        String getUrl = "/assets/posters/" + uploadItem.fileName
        HttpResponse asset = whenGetFile(getUrl, user1)

        // Then
        String responseBody = thenResponseCodeIs(asset, HttpServletResponse.SC_OK)
        thenResponseHeaderHas(asset, "Content-Type", "image/png")
    }

    @Test
    public void readThumbAssetByFileName() throws ClientProtocolException, IOException {
        // Given
        givenUser(user1)
        givenUploadFolder(uploadFolderPosters)
        givenPermissionForUser(user1, ["uploads:read:posters"])
        UploadFile uploadItem = givenUploadInFolder("posters", validPNGImage)

        // When
        String getUrl = "/assets/posters/icon/" + uploadItem.fileName
        HttpResponse asset = whenGetFile(getUrl, user1)

        // Then
        String responseBody = thenResponseCodeIs(asset, HttpServletResponse.SC_OK)
        thenResponseHeaderHas(asset, "Content-Type", "image/png")
    }

}
