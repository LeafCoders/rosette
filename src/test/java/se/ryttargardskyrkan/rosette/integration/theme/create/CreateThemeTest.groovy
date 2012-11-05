package se.ryttargardskyrkan.rosette.integration.theme.create

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
import se.ryttargardskyrkan.rosette.model.Theme
import se.ryttargardskyrkan.rosette.security.RosettePasswordService

import com.mongodb.util.JSON

public class CreateThemeTest extends AbstractIntegrationTest {

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
		}]
		"""));
		 
		mongoTemplate.getCollection("groups").insert(JSON.parse("""
		[{
			"_id" : "1",
			"name" : "Admins",
			"permissions" : ["*"]
		}]
		"""));
		
		mongoTemplate.getCollection("groupMemberships").insert(JSON.parse("""
		[{
			"_id" : "1",
			"userId" : "1",
			"groupId" : "1"
		}]
		"""));

		// When
		HttpPost postRequest = new HttpPost(baseUrl + "/themes")
		String requestBody = """
		{
			"title" : "Markusevangeliet",
			"description" : "Vi läser igenom markusevangeliet"
		}
		"""
		postRequest.setEntity(new StringEntity(requestBody, "application/json", "UTF-8"))
		postRequest.addHeader(new BasicScheme().authenticate(new UsernamePasswordCredentials("lars.arvidsson@gmail.com", "password"), postRequest));
		HttpResponse response = httpClient.execute(postRequest)

		// Then
		
		// Asserting response
		String responseJson = TestUtil.responseBodyAsString(response)
		Theme responseTheme = new ObjectMapper().readValue(responseJson, Theme.class)
		
		assertEquals(HttpServletResponse.SC_CREATED, response.getStatusLine().getStatusCode())
		assertEquals("application/json;charset=UTF-8", response.getHeaders("Content-Type")[0].getValue())
		TestUtil.assertJsonEquals("""
		{
			"id" : "${responseTheme.getId()}",
			"title" : "Markusevangeliet",
			"description" : "Vi läser igenom markusevangeliet"
		}
		""", responseJson)
		
		// Asserting database
		
		assertEquals(1L, mongoTemplate.count(new Query(), Theme.class))
		Theme themeInDatabase = mongoTemplate.findOne(new Query(), Theme.class)
		TestUtil.assertJsonEquals("""
		{
			"id" : "${responseTheme.getId()}",
			"title" : "Markusevangeliet",
			"description" : "Vi läser igenom markusevangeliet"
		}
		""", new ObjectMapper().writeValueAsString(themeInDatabase))
	}
}
