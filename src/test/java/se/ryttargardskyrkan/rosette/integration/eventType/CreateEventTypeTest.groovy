package se.ryttargardskyrkan.rosette.integration.eventType

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
import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.integration.util.TestUtil
import se.ryttargardskyrkan.rosette.model.EventType
import se.ryttargardskyrkan.rosette.security.RosettePasswordService

import javax.servlet.http.HttpServletResponse

import static junit.framework.Assert.assertEquals

public class CreateEventTypeTest extends AbstractIntegrationTest {

    @Test
    public void test() throws ClientProtocolException, IOException {
        // Given
		givenUser(user1)
		givenPermissionForUser(user1, ["create:eventTypes", "read:*"])
		givenGroup(group1)
		givenResourceType(userResourceType1)
		givenResourceType(uploadResourceType1)

		// When
		postRequest = new HttpPost(baseUrl + "/eventTypes")
		HttpResponse postResponse = whenPost(postRequest, user1, """{
			"key" : "speakers",
			"name" : "Speakers",
			"resourceTypes" : [
				{ "idRef": "${userResourceType1.id}" },
				{ "idRef": "${uploadResourceType1.id}" }
			]
		}""")

		// Then
		thenResponseCodeIs(postResponse, HttpServletResponse.SC_CREATED)
		thenResponseHeaderHas(postResponse, "Content-Type", "application/json;charset=UTF-8")

		String responseBody = TestUtil.jsonFromResponse(postResponse)
		String expectedData = """{
			"id" : "${ JSON.parse(responseBody)['id'] }",
			"key" : "speakers",
			"name" : "Speakers",
			"resourceTypes" : [
				{ "idRef" : "${userResourceType1.id}","referredObject" : null },
				{ "idRef" : "${uploadResourceType1.id}", "referredObject" : null }
			]
		}"""
		thenResponseDataIs(responseBody, expectedData)
		postRequest.releaseConnection()
		thenDataInDatabaseIs(EventType.class, "[${expectedData}]")
		thenItemsInDatabaseIs(EventType.class, 1)
    }
}
