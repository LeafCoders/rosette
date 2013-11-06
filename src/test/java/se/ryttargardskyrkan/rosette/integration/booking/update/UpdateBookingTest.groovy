package se.ryttargardskyrkan.rosette.integration.booking.update

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
import se.ryttargardskyrkan.rosette.model.Booking

import javax.servlet.http.HttpServletResponse

import static junit.framework.Assert.assertEquals
import se.ryttargardskyrkan.rosette.security.RosettePasswordService

public class UpdateBookingTest extends AbstractIntegrationTest {

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
            "patterns" : ["update:bookings:2"]
        }]
        """))

        mongoTemplate.getCollection("bookings").insert(JSON.parse("""
		[{
			"_id" : "1",
			"customerName" : "Customer 1",
			"startTime" : ${TestUtil.mongoDate("2012-10-25 13:00 Europe/Stockholm")},
			"endTime" : ${TestUtil.mongoDate("2012-10-25 15:00 Europe/Stockholm")},
            "location" : { "idRef" : "1", "text" : null }
		},
		{
			"_id" : "2",
			"customerName" : "Customer 1",
			"startTime" : ${TestUtil.mongoDate("2012-10-26 08:00 Europe/Stockholm")},
			"endTime" : ${TestUtil.mongoDate("2012-10-26 12:00 Europe/Stockholm")},
            "location" : { "idRef" : null, "text" : "Oasen" }
		}]
  		"""))


		// When
		HttpPut putRequest = new HttpPut(baseUrl + "/bookings/2")
		String requestBody = """
		{
			"customerName" : "Customer 2",
			"startTime" : "2012-10-26 08:30 Europe/Stockholm",
			"endTime" : "2012-10-26 11:30 Europe/Stockholm",
            "location" : { "idRef" : null, "text" : "Aspen" }
		}
		"""
		putRequest.setEntity(new StringEntity(requestBody, "application/json", "UTF-8"))
		putRequest.addHeader(new BasicScheme().authenticate(new UsernamePasswordCredentials("user@host.com", "password"), putRequest))
		HttpResponse response = httpClient.execute(putRequest)

		// Then

		// Asserting response
		assertEquals(HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode())

		// Asserting groups in database
		Query queryBookings = new Query()
		List<Booking> bookingsInDatabase = mongoTemplate.find(queryBookings, Booking.class)

		assertEquals(2L, mongoTemplate.count(new Query(), Booking.class))
		TestUtil.assertJsonEquals("""
		[{
			"id" : "1",
			"customerName" : "Customer 1",
			"startTime" : "2012-10-25 13:00 Europe/Stockholm",
			"endTime" : "2012-10-25 15:00 Europe/Stockholm",
            "location" : { "idRef" : "1", "text" : null, "referredObject" : null }
		},
		{
			"id" : "2",
			"customerName" : "Customer 2",
			"startTime" : "2012-10-26 08:30 Europe/Stockholm",
			"endTime" : "2012-10-26 11:30 Europe/Stockholm",
            "location" : { "idRef" : null, "text" : "Aspen", "referredObject" : null }
		}]
		""", new ObjectMapper().writeValueAsString(bookingsInDatabase))
	}
}
