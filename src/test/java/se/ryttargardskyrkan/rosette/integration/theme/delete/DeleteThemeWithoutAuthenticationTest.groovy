package se.ryttargardskyrkan.rosette.integration.theme.delete;

import static junit.framework.Assert.*

import javax.servlet.http.HttpServletResponse

import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpDelete
import org.junit.Test
import org.springframework.data.mongodb.core.query.Query

import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.model.Theme

import com.mongodb.util.JSON

public class DeleteThemeWithoutAuthenticationTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given
		mongoTemplate.getCollection("themes").insert(JSON.parse("""
		[{
			"_id" : "1",
			"title" : "Markusevangeliet",
			"description" : "Vi läser igenom markusevangeliet"
		},
		{
			"_id" : "2",
			"title" : "Johannesevangeliet",
			"description" : "Vi läser igenom johannesevangeliet"
		}]

		"""))

		// When
		HttpDelete deleteRequest = new HttpDelete(baseUrl + "/themes/1")
		deleteRequest.setHeader("Accept", "application/json; charset=UTF-8")
		deleteRequest.setHeader("Content-Type", "application/json; charset=UTF-8")
		HttpResponse response = httpClient.execute(deleteRequest)

		// Then
		assertEquals(HttpServletResponse.SC_FORBIDDEN, response.getStatusLine().getStatusCode())
		assertEquals("Forbidden", response.getStatusLine().getReasonPhrase())
		assertEquals(2L, mongoTemplate.count(new Query(), Theme.class))
	}
}
