package se.ryttargardskyrkan.rosette.integration.userresourcetype.delete

import com.mongodb.util.JSON
import org.apache.http.HttpResponse
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpDelete
import org.apache.http.impl.auth.BasicScheme
import org.codehaus.jackson.map.ObjectMapper
import org.junit.Test
import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.integration.util.TestUtil
import se.ryttargardskyrkan.rosette.model.UserResourceType
import se.ryttargardskyrkan.rosette.security.RosettePasswordService

import javax.servlet.http.HttpServletResponse

import static junit.framework.Assert.assertEquals

public class DeleteUserResourceTypeTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given
		String hashedPassword = new RosettePasswordService().encryptPassword("password");
		mongoTemplate.getCollection("users").insert(JSON.parse("""
		[{
			"_id" : "1",
			"username" : "user@host.com",
			"hashedPassword" : "${hashedPassword}",
			"status" : "active"
		}]
		"""));
	
		mongoTemplate.getCollection("permissions").insert(JSON.parse("""
		[{
			"_id" : "1",
			"userId" : "1",
			"patterns" : ["*"]
		}]
		"""));

        mongoTemplate.getCollection("userResourceTypes").insert(JSON.parse("""
        [{
            "_id" : "1",
            "name" : "motesledare",
			"groupId" : "1"
        },
        {
            "_id" : "2",
            "name" : "tolkar",
			"groupId" : "2"
        }]
        """))

		// When
		HttpDelete deleteRequest = new HttpDelete(baseUrl + "/userResourceTypes/2")
		deleteRequest.setHeader("Accept", "application/json; charset=UTF-8")
		deleteRequest.setHeader("Content-Type", "application/json; charset=UTF-8")
		deleteRequest.addHeader(new BasicScheme().authenticate(new UsernamePasswordCredentials("user@host.com", "password"), deleteRequest));
		HttpResponse response = httpClient.execute(deleteRequest)

		// Then

		// Asserting response
		assertEquals(HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode())

		// Asserting groups in database
		List<UserResourceType> userResourceTypesInDatabase = mongoTemplate.findAll(UserResourceType.class)
		TestUtil.assertJsonEquals("""
		[{
			"id" : "1",
            "name" : "motesledare",
			"groupId" : "1",
			"sortOrder" : 0
		}]""", new ObjectMapper().writeValueAsString(userResourceTypesInDatabase))
	}
}
