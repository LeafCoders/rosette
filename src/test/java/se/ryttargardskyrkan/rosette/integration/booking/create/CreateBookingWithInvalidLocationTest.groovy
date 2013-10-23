package se.ryttargardskyrkan.rosette.integration.booking.create

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
import se.ryttargardskyrkan.rosette.model.Booking
import se.ryttargardskyrkan.rosette.security.RosettePasswordService

import javax.servlet.http.HttpServletResponse

import static junit.framework.Assert.assertEquals

public class CreateBookingWithInvalidLocationTest extends AbstractIntegrationTest {

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
			"patterns" : ["create:bookings"]
		}]
		"""));

        // When
        HttpPost postRequest = new HttpPost(baseUrl + "/bookings")
        String requestBody = """
		{
			"customerName" : "",
			"startTime" : "2012-03-25 13:00 Europe/Stockholm",
			"endTime" : "2012-03-19 09:00 Europe/Stockholm"
		}
		"""
        postRequest.setEntity(new StringEntity(requestBody, "application/json", "UTF-8"))
        postRequest.addHeader(new BasicScheme().authenticate(new UsernamePasswordCredentials("user@host.com", "password"), postRequest));
        HttpResponse response = httpClient.execute(postRequest)

        // Then

		// Asserting response
		assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatusLine().getStatusCode())
        assertEquals("application/json;charset=UTF-8", response.getHeaders("Content-Type")[0].getValue())
		TestUtil.assertJsonEquals("""
		[
			{ "property" : "customerName", "message" : "booking.customerName.notEmpty" },
			{ "property" : "location",     "message" : "booking.location.notNull" },
			{ "property" : "",             "message" : "booking.startBeforeEndTime" }
		]
		""", TestUtil.jsonFromResponse(response))

        // Asserting database
        List<Booking> bookingsInDatabase = mongoTemplate.findAll(Booking.class)
        assertEquals(0L, mongoTemplate.count(new Query(), Booking.class))
    }
}
