package se.ryttargardskyrkan.rosette.integration.upload.delete

import com.mongodb.util.JSON
import org.apache.http.HttpResponse
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpDelete
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.auth.BasicScheme
import org.codehaus.jackson.map.ObjectMapper
import org.codehaus.jackson.type.TypeReference
import org.junit.Test
import org.springframework.data.mongodb.core.query.Query
import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.integration.util.TestUtil
import se.ryttargardskyrkan.rosette.model.UploadRequest
import se.ryttargardskyrkan.rosette.model.UploadResponse
import se.ryttargardskyrkan.rosette.security.RosettePasswordService

import javax.servlet.http.HttpServletResponse

import static junit.framework.Assert.assertEquals
import static junit.framework.Assert.assertTrue

public class DeleteUploadTest extends AbstractIntegrationTest {

    @Test
    public void test() throws ClientProtocolException, IOException {
        // Given
        String hashedPassword = new RosettePasswordService().encryptPassword("password")
        mongoTemplate.getCollection("users").insert(JSON.parse("""
		[{
			"_id" : "1",
			"username" : "user@host.com",
			"hashedPassword" : "${hashedPassword}",
			"status" : "active"
		}]
		"""))

        mongoTemplate.getCollection("permissions").insert(JSON.parse("""
		[{
			"_id" : "1",
			"userId" : "1",
			"patterns" : ["create:uploads:test", "delete:uploads:test"]
		},
        {
			"_id" : "2",
			"everyone" : true,
			"patterns" : ["read:assets:test"]
		}]
		"""))

        // When
        HttpPost postRequest = new HttpPost(baseUrl + "/uploads/TEST")
        String requestBody = """
        {
            "fileName" : "image.png",
            "mimeType" : "image/png",
            "fileData" : "iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAABAWlDQ1BJQ0MgUHJvZmlsZQAAGBljYGC8k1hQkMPEwMCQm1dSFOTupBARGaUA5MJBYnJxgWNAgA9IIC8/LxUuAWd8u8bACOJc1nV2D1ao2TrN8VKr2JnJE+fttOuadAGuDDuDo7ykoAQo9QSIRYqAlgPpHyB2OpjNyANiJ0HYCiB2UUiQMwMDowmQzZcOYbuA2EkQdgiInZJanAxUkwJklyH88zkE7E5GsZMIsfwFDAyW8gwMzN0IsaRpDAzb9zMwSJxBiKkA1fHbMDBsO5dcWlQGNBcEGBnPMjAQ4kPcAlYv456al1qUmawQUJRZlliSqgAK74Ci/LTMHCzBCtZCPgEAA1NJWOMhui0AAAAJcEhZcwAACxMAAAsTAQCanBgAAAI9aVRYdFhNTDpjb20uYWRvYmUueG1wAAAAAAA8eDp4bXBtZXRhIHhtbG5zOng9ImFkb2JlOm5zOm1ldGEvIiB4OnhtcHRrPSJYTVAgQ29yZSA1LjQuMCI+CiAgIDxyZGY6UkRGIHhtbG5zOnJkZj0iaHR0cDovL3d3dy53My5vcmcvMTk5OS8wMi8yMi1yZGYtc3ludGF4LW5zIyI+CiAgICAgIDxyZGY6RGVzY3JpcHRpb24gcmRmOmFib3V0PSIiCiAgICAgICAgICAgIHhtbG5zOnRpZmY9Imh0dHA6Ly9ucy5hZG9iZS5jb20vdGlmZi8xLjAvIj4KICAgICAgICAgPHRpZmY6WFJlc29sdXRpb24+NzI8L3RpZmY6WFJlc29sdXRpb24+CiAgICAgICAgIDx0aWZmOlJlc29sdXRpb25Vbml0PjI8L3RpZmY6UmVzb2x1dGlvblVuaXQ+CiAgICAgICAgIDx0aWZmOllSZXNvbHV0aW9uPjcyPC90aWZmOllSZXNvbHV0aW9uPgogICAgICAgICA8dGlmZjpPcmllbnRhdGlvbj4xPC90aWZmOk9yaWVudGF0aW9uPgogICAgICAgICA8dGlmZjpQaG90b21ldHJpY0ludGVycHJldGF0aW9uPjI8L3RpZmY6UGhvdG9tZXRyaWNJbnRlcnByZXRhdGlvbj4KICAgICAgPC9yZGY6RGVzY3JpcHRpb24+CiAgIDwvcmRmOlJERj4KPC94OnhtcG1ldGE+CjAVYfMAAABzSURBVDgR1VFBCsAwCFvH3lrf5Gs3OgjEOEehh7FeotXEiK33fm4Lb1/g3tTvBY5qBXcPJTMLOZLXFQapIkKg6RV0MhqBKhgcKPnJgfYEgTEFE4D8BxeMSYCLM3EpwFY5VtF0Rm7mWInI0xVQmMVyhf8IXIo8H+rM3fTBAAAAAElFTkSuQmCC"
        }
        """
		
        postRequest.setEntity(new StringEntity(requestBody, "application/json", "UTF-8"))
        postRequest.addHeader(new BasicScheme().authenticate(new UsernamePasswordCredentials("user@host.com", "password"), postRequest));
        HttpResponse response = httpClient.execute(postRequest)

        // Then
        assertEquals(HttpServletResponse.SC_CREATED, response.getStatusLine().getStatusCode())
        assertEquals("application/json;charset=UTF-8", response.getHeaders("Content-Type")[0].getValue())

        // Asserting response
        String responseJson = TestUtil.jsonFromResponse(response)
        UploadResponse responseUpload = new ObjectMapper().readValue(responseJson, new TypeReference<UploadResponse>() {})

        TestUtil.assertJsonEquals("""
		{
			"id" : "${responseUpload.getId()}",
			"fileName" : "image.png",
            "folder": "test",
			"fileUrl" : "${baseUrl}/assets/test/${responseUpload.fileName}",
            "mimeType" : "image/png",
			"fileSize" : 1047,
            "width" : 16,
            "height" : 16
		}
		""", responseJson)
		
		// Test assets
		HttpGet getAssetsRequest = new HttpGet(responseUpload.fileUrl)
		HttpResponse assetsResponse = httpClient.execute(getAssetsRequest)
		assertEquals(HttpServletResponse.SC_OK, assetsResponse.getStatusLine().getStatusCode())
		assertTrue(assetsResponse.allHeaders.toString().contains("image.png"))
    }
}
