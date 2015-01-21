package se.leafcoders.rosette.integration.location

import com.mongodb.util.JSON
import org.apache.http.HttpResponse
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpPut
import org.apache.http.entity.StringEntity
import org.apache.http.impl.auth.BasicScheme
import org.codehaus.jackson.map.ObjectMapper
import org.junit.Test
import org.springframework.data.mongodb.core.query.Query
import se.leafcoders.rosette.integration.AbstractIntegrationTest
import se.leafcoders.rosette.integration.util.TestUtil
import se.leafcoders.rosette.model.Location
import se.leafcoders.rosette.security.RosettePasswordService

import javax.servlet.http.HttpServletResponse

import static junit.framework.Assert.assertEquals

public class UpdateLocationTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["update:locations", "read:locations", "read:uploads"])
		Object image = givenUploadInFolder("locations", validPNGImage)
		givenLocation(location1)

		// When
		String putUrl = "/locations/${ location1.id }"
		HttpResponse putResponse = whenPut(putUrl, user1, """{
			"name" : "Stockholm",
			"description" : "Capital",
			"directionImage" : ${ toJSON(image) }
		}""")

		// Then
		thenResponseCodeIs(putResponse, HttpServletResponse.SC_OK)

		String expectedData = """[{
			"id" : "${ location1.id }",
			"name" : "Stockholm",
			"description" : "Capital",
			"directionImage" : ${ toJSON(image) }
		}]"""
		thenDataInDatabaseIs(Location.class, expectedData)
		thenItemsInDatabaseIs(Location.class, 1)
	}
}
