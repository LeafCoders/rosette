package se.ryttargardskyrkan.rosette.integration.theme.update

import static org.junit.Assert.*

import javax.servlet.http.HttpServletResponse

import org.apache.http.HttpResponse
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpPut
import org.apache.http.entity.StringEntity
import org.apache.http.impl.auth.BasicScheme
import org.apache.shiro.authc.credential.DefaultPasswordService
import org.apache.shiro.authc.credential.PasswordService
import org.codehaus.jackson.map.ObjectMapper
import org.codehaus.jackson.type.TypeReference
import org.junit.Test
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query

import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.integration.util.TestUtil
import se.ryttargardskyrkan.rosette.model.Theme
import se.ryttargardskyrkan.rosette.model.Group
import se.ryttargardskyrkan.rosette.model.User

public class UpdateMissingThemeTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given
		PasswordService passwordService = new DefaultPasswordService();
		String hashedPassword = passwordService.encryptPassword("password");
		String groups = """
		[{
			"id" : "1",
			"name" : "admin",
			"permissions" : ["themes:update"]
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
		String themes = """
		[{
			"id" : "1",
			"title" : "Markusevangeliet",
			"description" : "Vi läser igenom markusevangeliet"
		},
		{
			"id" : "2",
			"title" : "Johannesevangeliet",
			"description" : "Vi läser igenom johannesevangeliet"
		}]
		"""
		mongoTemplate.insert(new ObjectMapper().readValue(themes, new TypeReference<ArrayList<Theme>>() {}), "themes")

		// When
		HttpPut putRequest = new HttpPut(baseUrl + "/themes/3")
		String requestBody = """
		{
			"id" : "1",
			"title" : "Markusevangeliet uppdaterad",
			"description" : "Vi läser igenom markusevangeliet"
		}
		"""
		putRequest.setEntity(new StringEntity(requestBody, "application/json", "UTF-8"))
		putRequest.addHeader(new BasicScheme().authenticate(new UsernamePasswordCredentials("lars.arvidsson@gmail.com", "password"), putRequest));
		HttpResponse response = httpClient.execute(putRequest)

		// Then
		assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatusLine().getStatusCode())
		assertEquals("Not Found", response.getStatusLine().getReasonPhrase())
		assertEquals(2L, mongoTemplate.count(new Query(), Theme.class))
	}
}
