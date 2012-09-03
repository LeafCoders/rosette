package se.ryttargardskyrkan.rosette.integration.event.create

import static org.junit.Assert.*

import org.apache.http.HttpResponse
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.auth.BasicScheme
import org.apache.shiro.authc.credential.DefaultPasswordService
import org.apache.shiro.authc.credential.PasswordService
import org.codehaus.jackson.map.ObjectMapper
import org.codehaus.jackson.type.TypeReference
import org.junit.Test
import org.springframework.data.mongodb.core.MongoTemplate

import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.integration.util.EventTestUtil;
import se.ryttargardskyrkan.rosette.integration.util.TestUtil;
import se.ryttargardskyrkan.rosette.model.Group
import se.ryttargardskyrkan.rosette.model.User

public class CreateValidEventTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given
		PasswordService passwordService = new DefaultPasswordService();
		String hashedPassword = passwordService.encryptPassword("password");
		String groups = """
		[{
			"id" : "1",
			"name" : "admin",
			"permissions" : ["events:create"]
		}]
		"""
		mongoTemplate.insert(new ObjectMapper().readValue(groups, new TypeReference<ArrayList<Group>>() {}), "groups");
		String users = """
		[{
			"username" : "lars.arvidsson@gmail.com",
			"hashedPassword" : "${hashedPassword}",
			"status" : "active",
			"groupMemberships" : [{"groupId" : "1"}]
		}]
		"""
		mongoTemplate.insert(new ObjectMapper().readValue(users, new TypeReference<ArrayList<User>>() {}), "users")

		// When
		HttpPost postRequest = new HttpPost(baseUrl + "/events")
		String requestBody = """
		{
			"title" : "Gudstjänst",
			"startTime" : """ + TestUtil.dateTimeAsUnixTime("2012-03-25 11:00") + """
		}
		"""
		postRequest.setEntity(new StringEntity(requestBody, "application/json", "UTF-8"))
		postRequest.addHeader(new BasicScheme().authenticate(new UsernamePasswordCredentials("lars.arvidsson@gmail.com", "password"), postRequest));
		HttpResponse response = httpClient.execute(postRequest)

		// Then
		assertEquals(201, response.getStatusLine().getStatusCode())
		assertEquals("application/json;charset=UTF-8", response.getHeaders("Content-Type")[0].getValue())

		String expectedEvents = """
		{
			"title" : "Gudstjänst",
			"startTime" : """ + TestUtil.dateTimeAsUnixTime("2012-03-25 11:00") + """,
			"endTime" : null
		}
		"""
		EventTestUtil.assertEventResponseBodyIsCorrect(expectedEvents, response);
	}
}
