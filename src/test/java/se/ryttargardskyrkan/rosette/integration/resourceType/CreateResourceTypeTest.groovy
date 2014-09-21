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
		postRequest = new HttpPost(baseUrl + "/resourceTypes")
		HttpResponse postResponse = whenPost(postRequest, user1, """{
			"type" : "user",
			"key" : "speaker",
			"category" : "Personer",
			"name" : "Talare",
			"description" : "Den som talar",
			"multiSelect" : false,
			"allowText" : false,
			"group" : { "idRef": "${group1.id}" }
		}""")

		// Then
		thenResponseCodeIs(postResponse, HttpServletResponse.SC_CREATED)
		thenResponseHeaderHas(postResponse, "Content-Type", "application/json;charset=UTF-8")

		String responseBody = TestUtil.jsonFromResponse(postResponse)
		String expectedData = """{
			"type" : "user",
			"id" : "${ JSON.parse(responseBody)['id'] }",
			"key" : "speaker",
			"category" : "Personer",
			"name" : "Talare",
			"description" : "Den som talar",
			"multiSelect" : false,
			"allowText" : false,
			"group" : { "idRef": "${group1.id}", "referredObject": null }
		}"""
		thenResponseDataIs(responseBody, expectedData)
		postRequest.releaseConnection()
		expectedData = expectedData.replace('"type" : "user",', '')
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
		postRequest = new HttpPost(baseUrl + "/resourceTypes")
		HttpResponse postResponse = whenPost(postRequest, user1, """{
			"name" : "Talare",
			"description" : "Den som talar",
			"multiSelect" : false,
			"allowText" : false,
			"group" : { "idRef": "${group1.id}" }
		}""")

		// Then
		thenResponseCodeIs(postResponse, HttpServletResponse.SC_BAD_REQUEST)
		postRequest.releaseConnection()
    }

	@Test
	public void failsWhenCreateWithoutUniqueKey() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["create:resourceTypes"])
		givenGroup(group1)
		
		// When
		postRequest = new HttpPost(baseUrl + "/resourceTypes")
		HttpResponse postResponse = whenPost(postRequest, user1, """{
			"type" : "user",
			"key" : "speaker",
			"category" : "Personer",
			"name" : "Talare",
			"description" : "Den som talar",
			"multiSelect" : false,
			"allowText" : false,
			"group" : { "idRef": "${group1.id}" }
		}""")
	
		// Then
		thenResponseCodeIs(postResponse, HttpServletResponse.SC_CREATED)
		postRequest.releaseConnection();
		
		// When
		postRequest = new HttpPost(baseUrl + "/resourceTypes")
		HttpResponse postResponse2 = whenPost(postRequest, user1, """{
			"type" : "user",
			"key" : "speaker",
			"category" : "Personer",
			"name" : "Talare",
			"description" : "Den som talar",
			"multiSelect" : false,
			"allowText" : false,
			"group" : { "idRef": "${group1.id}" }
		}""")
	
		// Then
		thenResponseCodeIs(postResponse2, HttpServletResponse.SC_BAD_REQUEST)
	}
}
