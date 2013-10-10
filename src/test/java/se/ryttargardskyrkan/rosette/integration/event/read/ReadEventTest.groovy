package se.ryttargardskyrkan.rosette.integration.event.read

import static junit.framework.Assert.*

import javax.servlet.http.HttpServletResponse

import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpGet
import org.junit.Test

import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.integration.util.TestUtil

import com.mongodb.util.JSON

public class ReadEventTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given
		mongoTemplate.getCollection("events").insert(JSON.parse("""
		[{
			"_id" : "1",
			"title" : "Gudstjänst 1"
		},
		{
			"_id" : "2",
			"title" : "Gudstjänst 2",
			"startTime" : ${TestUtil.mongoDate("2012-04-25 11:00 Europe/Stockholm")},
			"description" : "Dopgudstjänst",
			"endTime" : null,
			"requiredUserResourceTypes" : ["motesledare"],
			"userResources" :
			    [{
			        "userResourceTypeId" : "tolk",
			        "userReferences" :
			            [{
			                "userId" : "1",
			                "userFullName" : "Nisse Hult"
                        }]
			    },{
			        "userResourceTypeId" : "motesledare",
			        "userReferences" :
			            [{
			                "userId" : "2",
			                "userFullName" : "Lars Arvidsson"
                        }]
			    }]
		},
		{
			"_id" : "3",
			"title" : "Gudstjänst 3"
		}]
		"""))
		
		mongoTemplate.getCollection("permissions").insert(JSON.parse("""
		[{
			"_id" : "1",
			"everyone" : true,
			"patterns" : ["*"]
		}]
		"""));

		// When
		HttpGet getRequest = new HttpGet(baseUrl + "/events/2")
		getRequest.setHeader("Accept", "application/json; charset=UTF-8")
		getRequest.setHeader("Content-Type", "application/json; charset=UTF-8")
		HttpResponse response = httpClient.execute(getRequest)

		// Then
		assertEquals(HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode())
		assertEquals("application/json;charset=UTF-8", response.getHeaders("Content-Type")[0].getValue())
		String expectedEvent = """
		{
			"id" : "2",
			"title" : "Gudstjänst 2",
			"startTime" : "2012-04-25 11:00 Europe/Stockholm",
			"endTime" : null,
			"description" : "Dopgudstjänst",
			"requiredUserResourceTypes" : ["motesledare"],
			"userResources" :
			    [{
			        "userResourceTypeId" : "tolk",
			        "userResourceTypeName" : null,
			        "userReferences" :
			            [{
			                "userId" : "1",
			                "userFullName" : "Nisse Hult"
                        }]
			    },{
			        "userResourceTypeId" : "motesledare",
			        "userResourceTypeName" : null,
			        "userReferences" :
			            [{
			                "userId" : "2",
			                "userFullName" : "Lars Arvidsson"
                        }]
			    }]
		}
		"""
		TestUtil.assertJsonResponseEquals(expectedEvent, response)
	}
}
