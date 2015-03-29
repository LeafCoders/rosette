package se.leafcoders.rosette.integration.location

import com.mongodb.util.JSON
import org.apache.http.HttpResponse
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.auth.BasicScheme
import org.codehaus.jackson.map.ObjectMapper
import org.codehaus.jackson.type.TypeReference
import org.junit.Test
import org.springframework.data.mongodb.core.query.Query
import se.leafcoders.rosette.integration.AbstractIntegrationTest
import se.leafcoders.rosette.integration.util.TestUtil
import se.leafcoders.rosette.model.Location
import se.leafcoders.rosette.security.RosettePasswordService

import javax.servlet.http.HttpServletResponse

import static junit.framework.Assert.assertEquals

public class CreateLocationTest extends AbstractIntegrationTest {

    @Test
    public void test() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["create:locations", "read:locations", "read:uploads"])
		givenUploadFolder(uploadFolderLocations)
		Object image = givenUploadInFolder("locations", validPNGImage)
		
		// When
		String postUrl = "/locations"
		HttpResponse postResponse = whenPost(postUrl, user1, """{
			"name" : "Kyrksalen",
			"description" : "En stor lokal med plats för ca 700 pers.",
			"directionImage": ${ toJSON(image) }
		}""")

		// Then
		String responseBody = thenResponseCodeIs(postResponse, HttpServletResponse.SC_CREATED)
		thenResponseHeaderHas(postResponse, "Content-Type", "application/json;charset=UTF-8")

		String expectedData = """{
			"id" : "${ JSON.parse(responseBody)['id'] }",
			"name" : "Kyrksalen",
			"description" : "En stor lokal med plats för ca 700 pers.",
			"directionImage": ${ toJSON(image) }
		}"""

		thenResponseDataIs(responseBody, expectedData)
		thenDataInDatabaseIs(Location.class, "[${expectedData}]")
		thenItemsInDatabaseIs(Location.class, 1)
    }
}
