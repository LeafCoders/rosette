package se.ryttargardskyrkan.rosette.integration.userresourcetype.update

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
import se.ryttargardskyrkan.rosette.model.UserResourceType
import se.ryttargardskyrkan.rosette.security.RosettePasswordService

import javax.servlet.http.HttpServletResponse

import static junit.framework.Assert.assertEquals

public class UpdateUserResourceTypeTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given
        String hashedPassword = new RosettePasswordService().encryptPassword("password")
        mongoTemplate.getCollection("users").insert(JSON.parse("""
        [{
            "_id" : "1",
            "username" : "user@host.com",
            "hashedPassword" : "${hashedPassword}",
            "status" : "active"
        }]
        """))

        mongoTemplate.getCollection("permissions").insert(JSON.parse("""
        [{
            "_id" : "1",
            "everyone" : true,
            "patterns" : ["*"]
        }]
        """))

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
		HttpPut putRequest = new HttpPut(baseUrl + "/userResourceTypes/2")
		String requestBody = """
		{
			"name" : "ljudtekniker",
			"groupId" : "3"
		}
		"""
		putRequest.setEntity(new StringEntity(requestBody, "application/json", "UTF-8"))
		putRequest.addHeader(new BasicScheme().authenticate(new UsernamePasswordCredentials("user@host.com", "password"), putRequest))
		HttpResponse response = httpClient.execute(putRequest)

		// Then

		// Asserting response
		assertEquals(HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode())

		// Asserting groups in database
		Query queryUserResourceTypes = new Query()
		List<UserResourceType> userResourceTypesInDatabase = mongoTemplate.find(queryUserResourceTypes, UserResourceType.class)

		assertEquals(2L, mongoTemplate.count(new Query(), UserResourceType.class))
		TestUtil.assertJsonEquals("""
		[{
			"id" : "1",
            "name" : "motesledare",
			"groupId" : "1",
			"sortOrder" : 0
		},
		{
			"id" : "2",
            "name" : "ljudtekniker",
			"groupId" : "3",
			"sortOrder" : 0
		}]
		""", new ObjectMapper().writeValueAsString(userResourceTypesInDatabase))
	}
}
