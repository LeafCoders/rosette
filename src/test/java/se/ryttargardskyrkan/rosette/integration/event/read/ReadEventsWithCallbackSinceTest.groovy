package se.ryttargardskyrkan.rosette.integration.event.read

import javax.servlet.http.HttpServletResponse

import static org.junit.Assert.*
import org.apache.commons.io.IOUtils
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpGet
import org.codehaus.jackson.map.ObjectMapper
import org.codehaus.jackson.type.TypeReference
import org.junit.Test
import org.springframework.data.mongodb.core.MongoTemplate

import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.integration.util.TestUtil
import se.ryttargardskyrkan.rosette.model.Event

public class ReadEventsWithCallbackSinceTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given
		String events = """
		[{
			"id" : "1",
			"title" : "Gudstjänst 1",
			"startTime" : "2012-03-25 11:00 Europe/Stockholm",
			"endTime" : null
		},
		{
			"id" : "3",
			"title" : "Gudstjänst 3",
			"startTime" : "2012-05-25 11:00 Europe/Stockholm",
			"endTime" : null
		},
		{
			"id" : "2",
			"title" : "Gudstjänst 2",
			"startTime" : "2012-04-25 11:00 Europe/Stockholm",
			"endTime" : null
		}]
		"""
		mongoTemplate.insert(new ObjectMapper().readValue(events, new TypeReference<ArrayList<Event>>() {}), "events")

		// When
		HttpGet getRequest = new HttpGet(baseUrl + "/events?since=" + TestUtil.dateTimeAsUnixTime("2012-04-25 11:00") + "&callback=handleJson")
		getRequest.setHeader("Accept", "application/json; charset=UTF-8")
		getRequest.setHeader("Content-Type", "application/json; charset=UTF-8")
		HttpResponse response = httpClient.execute(getRequest)

		// Then
		assertEquals(HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode())
		assertEquals("text/javascript;charset=UTF-8", response.getHeaders("Content-Type")[0].getValue())
		String exptectedReponse = """handleJson([{"id":"2","title":"Gudstjänst 2","startTime":"2012-04-25 11:00 Europe/Stockholm","endTime":null,"themeId":null},{"id":"3","title":"Gudstjänst 3","startTime":"2012-05-25 11:00 Europe/Stockholm","endTime":null,"themeId":null}]);"""
		String json = IOUtils.toString(response.getEntity().getContent(), "utf-8")
		assertEquals(exptectedReponse, json)
	}
}
