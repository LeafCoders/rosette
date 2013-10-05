package se.ryttargardskyrkan.rosette.integration.groupmembership.create

import static junit.framework.Assert.*

import javax.servlet.http.HttpServletResponse

import org.apache.http.HttpResponse
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.auth.BasicScheme
import org.codehaus.jackson.map.ObjectMapper
import org.junit.Test
import org.springframework.data.mongodb.core.query.Query

import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.integration.util.TestUtil
import se.ryttargardskyrkan.rosette.model.GroupMembership
import se.ryttargardskyrkan.rosette.security.RosettePasswordService

import com.mongodb.util.JSON

public class CreateGroupMembershipTest extends AbstractIntegrationTest {

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
		}]
		"""));
	
		mongoTemplate.getCollection("permissions").insert(JSON.parse("""
		[{
			"_id" : "1",
			"userId" : "1",
			"patterns" : ["*"]
		}]
		"""));

		// When
		HttpPost postRequest = new HttpPost(baseUrl + "/groupMemberships")
		String requestBody = """
		{
			"userId" : "2",
			"groupId" : "1"
		}
		"""
		postRequest.setEntity(new StringEntity(requestBody, "application/json", "UTF-8"))
		postRequest.addHeader(new BasicScheme().authenticate(new UsernamePasswordCredentials("lars.arvidsson@gmail.com", "password"), postRequest));
		HttpResponse response = httpClient.execute(postRequest)

		// Then
		
		// Asserting response
		String responseJson = TestUtil.jsonFromResponse(response)
		GroupMembership responseGroupMembership = new ObjectMapper().readValue(responseJson, GroupMembership.class)
		
		assertEquals(HttpServletResponse.SC_CREATED, response.getStatusLine().getStatusCode())
		assertEquals("application/json;charset=UTF-8", response.getHeaders("Content-Type")[0].getValue())
		TestUtil.assertJsonEquals("""
		{
			"id" : "${responseGroupMembership.getId()}",
			"userId" : "2",
			"groupId" : "1",
			"username":null,
			"groupName":null,
			"userFullName":null
		}
		""", responseJson)
		
		// Asserting database
		List<GroupMembership> groupMembershipsInDatabase = mongoTemplate.findAll(GroupMembership.class)
		
		assertEquals(2L, mongoTemplate.count(new Query(), GroupMembership.class))
		TestUtil.assertJsonEquals("""
		[{
			"id" : "1",
			"userId" : "1",
			"groupId" : "1",
            "username":null,
            "groupName":null,
            "userFullName":null
		},{
			"id" : "${responseGroupMembership.getId()}",
			"userId" : "2",
			"groupId" : "1",
			"username":null,
			"groupName":null,
			"userFullName":null
		}]
		""", new ObjectMapper().writeValueAsString(groupMembershipsInDatabase))
	}
}
