package se.ryttargardskyrkan.rosette.integration.resourceType

import static org.junit.Assert.assertEquals
import javax.servlet.http.HttpServletResponse
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpPost
import org.junit.Test
import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.integration.util.TestUtil
import se.ryttargardskyrkan.rosette.model.resource.ResourceType
import com.mongodb.util.JSON

public class CreateResourceTypeTest extends AbstractIntegrationTest {

    @Test
    public void createUserResourceTypeWithSuccess() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["create:resourceTypes"])
		givenGroup(group1)
		
		// When
		String postUrl = "/resourceTypes"
		HttpResponse postResponse = whenPost(postUrl, user1, """{
			"type" : "user",
			"id" : "speaker",
			"name" : "Talare",
			"description" : "Den som talar",
			"section" : "Personer",
			"multiSelect" : false,
			"allowText" : false,
			"group" : { "idRef": "${group1.id}" }
		}""")

		// Then
		String responseBody = thenResponseCodeIs(postResponse, HttpServletResponse.SC_CREATED)
		thenResponseHeaderHas(postResponse, "Content-Type", "application/json;charset=UTF-8")

		String expectedData = """{
			"type" : "user",
			"id" : "speaker",
			"name" : "Talare",
			"description" : "Den som talar",
			"section" : "Personer",
			"multiSelect" : false,
			"allowText" : false,
			"group" : { "idRef": "${group1.id}", "referredObject": null }
		}"""
		thenResponseDataIs(responseBody, expectedData)
		releasePostRequest()
		thenDataInDatabaseIs(ResourceType.class, "[${expectedData}]")
		thenItemsInDatabaseIs(ResourceType.class, 1)
    }

	@Test
	public void failsWhenCreateWithoutType() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["create:resourceTypes"])
		givenGroup(group1)
		
		// When
		String postUrl = "/resourceTypes"
		HttpResponse postResponse = whenPost(postUrl, user1, """{
			"name" : "Talare",
			"description" : "Den som talar",
			"multiSelect" : false,
			"allowText" : false,
			"group" : { "idRef": "${group1.id}" }
		}""")

		// Then
		thenResponseCodeIs(postResponse, HttpServletResponse.SC_BAD_REQUEST)
    }

	@Test
	public void failsWhenCreateWithoutUniqueKey() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["create:resourceTypes"])
		givenGroup(group1)
		
		// When
		String postUrl = "/resourceTypes"
		HttpResponse postResponse = whenPost(postUrl, user1, """{
			"id" : "speaker",
			"type" : "user",
			"name" : "Talare",
			"description" : "Den som talar",
			"section" : "Personer",
			"multiSelect" : false,
			"allowText" : false,
			"group" : { "idRef": "${group1.id}" }
		}""")
	
		// Then
		thenResponseCodeIs(postResponse, HttpServletResponse.SC_CREATED)
		releasePostRequest()
		
		// When
		HttpResponse postResponse2 = whenPost(postUrl, user1, """{
			"id" : "speaker",
			"type" : "user",
			"name" : "Talare",
			"description" : "Den som talar",
			"section" : "Personer",
			"multiSelect" : false,
			"allowText" : false,
			"group" : { "idRef": "${group1.id}" }
		}""")

		// Then
		thenResponseCodeIs(postResponse2, HttpServletResponse.SC_BAD_REQUEST)
	}
}
