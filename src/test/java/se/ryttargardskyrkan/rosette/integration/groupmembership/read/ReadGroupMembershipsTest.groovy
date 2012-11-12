package se.ryttargardskyrkan.rosette.integration.groupmembership.read

import static junit.framework.Assert.*

import javax.servlet.http.HttpServletResponse

import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpGet
import org.junit.Test

import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.integration.util.TestUtil
import se.ryttargardskyrkan.rosette.security.RosettePasswordService

import com.mongodb.util.JSON

public class ReadGroupMembershipsTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given
		String hashedPassword = new RosettePasswordService().encryptPassword("password");
		mongoTemplate.getCollection("users").insert(JSON.parse("""
		[{
			"_id" : "1",
			"username" : "lars.arvidsson@gmail.com",
			"hashedPassword" : "${hashedPassword}",
			"status" : "active"
		},{
			"_id" : "2",
			"username" : "nisse",
			"hashedPassword" : "${hashedPassword}",
			"status" : "active"
		}]
		"""));

		mongoTemplate.getCollection("groups").insert(JSON.parse("""
		[{
			"_id" : "1",
			"name" : "Admins"
		}]
		"""));

		mongoTemplate.getCollection("groupMemberships").insert(JSON.parse("""
		[{
			"_id" : "1",
			"userId" : "1",
			"groupId" : "1"
		},{
			"_id" : "2",
			"userId" : "2",
			"groupId" : "1"
		}]
		"""));
	
		mongoTemplate.getCollection("permissions").insert(JSON.parse("""
		[{
			"_id" : "1",
			"everyone" : true,
			"patterns" : ["*"]
		}]
		"""));

		// When
		HttpGet getRequest = new HttpGet(baseUrl + "/groupMemberships")
		getRequest.setHeader("Accept", "application/json; charset=UTF-8")
		getRequest.setHeader("Content-Type", "application/json; charset=UTF-8")
		HttpResponse response = httpClient.execute(getRequest)

		// Then
		assertEquals(HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode())
		assertEquals("application/json;charset=UTF-8", response.getHeaders("Content-Type")[0].getValue())
		String expectedThemes = """
		[{
			"id" : "1",
			"userId" : "1",
			"groupId" : "1"
		},{
			"id" : "2",
			"userId" : "2",
			"groupId" : "1"
		}]
		"""
		TestUtil.assertJsonResponseEquals(expectedThemes, response)
	}
}
