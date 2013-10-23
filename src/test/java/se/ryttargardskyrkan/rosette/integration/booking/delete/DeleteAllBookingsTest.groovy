package se.ryttargardskyrkan.rosette.integration.booking.delete

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
import se.ryttargardskyrkan.rosette.model.Booking
import se.ryttargardskyrkan.rosette.security.RosettePasswordService

import javax.servlet.http.HttpServletResponse

import static junit.framework.Assert.assertEquals

public class DeleteAllBookingsTest extends AbstractIntegrationTest {

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
			"patterns" : ["delete:bookings"]
		}]
		"""));

        mongoTemplate.getCollection("bookings").insert(JSON.parse("""
        [{
            "_id" : "1",
			"customerName" : "Customer 1",
			"startTime" : ${TestUtil.mongoDate("2012-03-25 13:00 Europe/Stockholm")},
			"endTime" : ${TestUtil.mongoDate("2012-03-25 15:00 Europe/Stockholm")},
            "location" : { "idRef" : "1", "text" : null }
        },
        {
            "_id" : "2",
			"customerName" : "Customer 1",
			"startTime" : ${TestUtil.mongoDate("2012-03-27 09:00 Europe/Stockholm")},
			"endTime" : ${TestUtil.mongoDate("2012-03-27 12:00 Europe/Stockholm")},
            "location" : { "idRef" : null, "text" : "Oasen" }
        }]
        """))

		// When
		HttpDelete deleteAllRequest = new HttpDelete(baseUrl + "/bookings")
		deleteAllRequest.setHeader("Accept", "application/json; charset=UTF-8")
		deleteAllRequest.setHeader("Content-Type", "application/json; charset=UTF-8")
		deleteAllRequest.addHeader(new BasicScheme().authenticate(new UsernamePasswordCredentials("user@host.com", "password"), deleteAllRequest));
		HttpResponse response = httpClient.execute(deleteAllRequest)

		// Then

		// Asserting response
		assertEquals(HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode())

		// Asserting groups in database
		List<Booking> bookingsInDatabase = mongoTemplate.findAll(Booking.class)
		TestUtil.assertJsonEquals("""[]""", new ObjectMapper().writeValueAsString(bookingsInDatabase))
	}
}
