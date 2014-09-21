package se.ryttargardskyrkan.rosette.integration.eventType

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
import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.integration.util.TestUtil
import se.ryttargardskyrkan.rosette.model.EventType
import se.ryttargardskyrkan.rosette.security.RosettePasswordService

import javax.servlet.http.HttpServletResponse

import static junit.framework.Assert.assertEquals

public class UpdateEventTypeTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["update:eventTypes"])
		givenGroup(group1)
		givenResourceType(userResourceType1)
		givenResourceType(uploadResourceType1)
		givenEventType(eventType1)
		givenEventType(eventType2)

		// When
		putRequest = new HttpPut(baseUrl + "/eventTypes/${eventType2.id}")
		HttpResponse putResponse = whenPut(putRequest, user1, """{
			"name" : "Changed name",
			"key" : "willNotChange",
			"resourceTypes" : [
				{ "idRef": "${uploadResourceType1.id}" },
				{ "idRef": "${userResourceType1.id}" }
			]
		}""")

		// Then
		thenResponseCodeIs(putResponse, HttpServletResponse.SC_OK)
		String expectedData = """[{
			"id" : "${eventType1.id}",
			"key" : "people",
			"name" : "EventType 1",
			"resourceTypes" : [
				{ "idRef" : "${userResourceType1.id}", "referredObject" : null },
				{ "idRef" : "${uploadResourceType1.id}", "referredObject" : null }
			]
		}, {
			"id" : "${eventType2.id}",
			"key" : "groups",
			"name" : "Changed name",
			"resourceTypes" : [
				{ "idRef" : "${uploadResourceType1.id}", "referredObject" : null },
				{ "idRef" : "${userResourceType1.id}", "referredObject" : null }
			]
		}]"""
		thenDataInDatabaseIs(EventType.class, expectedData)
		thenItemsInDatabaseIs(EventType.class, 2)
	}
}
