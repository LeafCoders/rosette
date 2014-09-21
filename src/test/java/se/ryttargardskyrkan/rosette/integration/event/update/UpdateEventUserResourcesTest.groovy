package se.ryttargardskyrkan.rosette.integration.event.update

import com.mongodb.util.JSON
import org.apache.http.HttpResponse
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpPut
import org.apache.http.entity.StringEntity
import org.apache.http.impl.auth.BasicScheme
import org.codehaus.jackson.map.ObjectMapper
import org.junit.Test
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Order
import org.springframework.data.mongodb.core.query.Query
import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.integration.util.TestUtil
import se.ryttargardskyrkan.rosette.model.event.Event;
import se.ryttargardskyrkan.rosette.security.RosettePasswordService
import javax.servlet.http.HttpServletResponse
import static junit.framework.Assert.assertEquals

public class UpdateEventUserResourcesTest extends AbstractIntegrationTest {

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

        mongoTemplate.getCollection("userResourceTypes").insert(JSON.parse("""
		[{
			"_id" : "0",
            "name" : "Tolkar",
			"groupId" : "2"
		},{
			"_id" : "1",
            "name" : "Mötesledare",
			"groupId" : "1"
		},{
		    "_id" : "2",
		    "name" : "Ljustekniker",
		    "groupId" : "3"
		}]
		"""))
		 
		mongoTemplate.getCollection("permissions").insert(JSON.parse("""
		[{
			"_id" : "1",
			"userId" : "1",
			"patterns" : ["update:events:*:title", "update:events:*:startTime", "update:events:*:description", "update:events:*:userResources:0", "update:events:*:userResources:1"]
		}]
		"""));
		
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
			"requiredUserResourceTypes" : ["0", "1"],
			"userResources" :
			    [{
			        "userResourceTypeId" : "2",
			        "userResourceTypeName" : "Ljustekniker",
			        "userReferences" :
			            [{
			                "userId" : "5",
			                "userFullName" : "Carl Larsson"
                        },{
			                "userId" : "6",
			                "userFullName" : "Astrid Lindgren"
                        }]
			    },{
			        "userResourceTypeId" : "0",
			        "userResourceTypeName" : "Tolk",
			        "userReferences" :
			            [{
			                "userId" : "1",
			                "userFullName" : "Nisse Hult"
                        }]
			    },{
			        "userResourceTypeId" : "1",
			        "userResourceTypeName" : "Mötesledare",
			        "userReferences" :
			            [{
			                "userId" : "2",
			                "userFullName" : "Lars Arvidsson"
                        }]
			    }]
		}]
		"""))

		// When
		HttpPut putRequest = new HttpPut(baseUrl + "/events/2")
		String requestBody = """
		{
			"id" : "1",
			"title" : "Gudstjänst 2 uppdaterad",
			"startTime" : "2012-03-25 11:00 Europe/Stockholm",
			"description" : "Nattvard",
			"requiredUserResourceTypes" : ["Tolk", "Mötesledare"],
			"userResources" :
			    [{
			        "userResourceTypeId" : "0",
			        "userResourceTypeName" : "Tolk",
			        "userReferences" :
			            [{
			                "userId" : "3",
			                "userFullName" : "Pelle Plutt"
                        }]
			    }]
		}
		"""
		putRequest.setEntity(new StringEntity(requestBody, "application/json", "UTF-8"))
		putRequest.addHeader(new BasicScheme().authenticate(new UsernamePasswordCredentials("lars.arvidsson@gmail.com", "password"), putRequest));
		HttpResponse response = httpClient.execute(putRequest)

		// Then
		assertEquals(HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode())
		assertEquals(2L, mongoTemplate.count(new Query(), Event.class))
		String expectedEvents = """
		[{
			"id" : "1",
			"title" : "Gudstjänst 1",
			"startTime" : null,
			"endTime" : null,
			"description" : null,
			"eventType":null,
			"location":null,
			"requiredUserResourceTypes":null,
			"userResources":null
		},
		{
			"id" : "2",
			"title" : "Gudstjänst 2 uppdaterad",
			"startTime" : "2012-03-25 11:00 Europe/Stockholm",
			"endTime" : null,
			"description" : "Nattvard",
			"eventType":null,
			"location":null,
			"requiredUserResourceTypes" : ["0", "1"],
			"userResources" :
			    [{
			        "userResourceTypeId" : "0",
			        "userResourceTypeName" : "Tolk",
			        "userReferences" :
			            [{
			                "userId" : "3",
			                "userFullName" : "Pelle Plutt"
                        }]
			    },{
			        "userResourceTypeId" : "2",
			        "userResourceTypeName" : "Ljustekniker",
			        "userReferences" :
			            [{
			                "userId" : "5",
			                "userFullName" : "Carl Larsson"
                        },{
			                "userId" : "6",
			                "userFullName" : "Astrid Lindgren"
                        }]
			    }]
		}]
		"""
		Query query = new Query();
		query.with(new Sort(Sort.Direction.ASC, "startTime"));
		List<Event> eventsInDatabase = mongoTemplate.find(query, Event.class);
		TestUtil.assertJsonEquals(expectedEvents, new ObjectMapper().writeValueAsString(eventsInDatabase))
	}
}
