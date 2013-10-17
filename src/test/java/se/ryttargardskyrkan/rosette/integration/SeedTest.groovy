package se.ryttargardskyrkan.rosette.integration

import com.mongodb.util.JSON
import org.apache.http.client.ClientProtocolException
import org.junit.Test
import se.ryttargardskyrkan.rosette.converter.RosetteDateConverter
import se.ryttargardskyrkan.rosette.security.RosettePasswordService
import se.ryttargardskyrkan.rosette.integration.util.TestUtil

class SeedTest extends AbstractIntegrationTest {

    @Test
    public void test() throws ClientProtocolException, IOException {
        String hashedPassword = new RosettePasswordService().encryptPassword("password");
        mongoTemplate.getCollection("users").insert(JSON.parse("""
		[{
			"_id" : "1",
			"username" : "lars.arvidsson@gmail.com",
            "firstName" : "Lars",
            "lastName" : "Arvidsson",
			"hashedPassword" : "${hashedPassword}",
			"status" : "active"
		},{
			"_id" : "2",
			"username" : "sjobs@apple.com",
            "firstName" : "Steve",
            "lastName" : "Jobs",
			"hashedPassword" : "${hashedPassword}",
			"status" : "active"
		},{
			"_id" : "3",
			"username" : "jerry@seinfeld.com",
            "firstName" : "Jerry",
            "lastName" : "Seinfeld",
			"hashedPassword" : "${hashedPassword}",
			"status" : "active"
		}]
		"""));

        mongoTemplate.getCollection("groups").insert(JSON.parse("""
		[{
			"_id" : "1",
			"name" : "Admins"
		},
		{
			"_id" : "2",
			"name" : "Mötesledare"
		},
		{
			"_id" : "3",
			"name" : "Tolkar"
		},
		{
			"_id" : "4",
			"name" : "Ljudtekniker"
		}]
		"""))

        mongoTemplate.getCollection("groupMemberships").insert(JSON.parse("""
		[{
			"_id" : "1",
			"userId" : "1",
			"userFullName" : "Lars Arvidsson",
			"groupId" : "1",
			"groupName" : "Admins"
		},{
			"_id" : "2",
			"userId" : "1",
			"userFullName" : "Lars Arvidsson",
			"groupId" : "4",
			"groupName" : "Ljudtekniker"
		},{
			"_id" : "3",
			"userId" : "2",
			"userFullName" : "Steve Jobs",
			"groupId" : "1",
			"groupName" : "Admins"
		},{
			"_id" : "4",
			"userId" : "2",
			"userFullName" : "Steve Jobs",
			"groupId" : "2",
			"groupName" : "Mötesledare"
		},{
			"_id" : "5",
			"userId" : "3",
			"userFullName" : "Jerry Seinfeld",
			"groupId" : "2",
			"groupName" : "Mötesledare"
		},{
			"_id" : "6",
			"userId" : "3",
			"userFullName" : "Jerry Seinfeld",
			"groupId" : "3",
			"groupName" : "Tolkar"
		}]
		"""));

        mongoTemplate.getCollection("userResourceTypes").insert(JSON.parse("""
		[{
			"_id" : "1",
            "name" : "Mötesledare",
			"groupId" : "2",
			"sortOrder" : 0
		},
		{
			"_id" : "2",
            "name" : "Tolkar",
			"groupId" : "3",
			"sortOrder" : 1
		},
		{
			"_id" : "3",
            "name" : "Ljudtekniker",
			"groupId" : "4",
			"sortOrder" : 2
		}]
		"""))

        mongoTemplate.getCollection("eventTypes").insert(JSON.parse("""
		[{
			"_id" : "1",
			"name" : "Gudstjänst"
		},
		{
			"_id" : "2",
			"name" : "Bön"
		}]
		"""))

        mongoTemplate.getCollection("locations").insert(JSON.parse("""
		[{
			"_id" : "1",
            "name" : "Kyrksalen",
			"description" : "En stor lokal med plats för ca 700 pers."
		},
		{
			"_id" : "2",
            "name" : "Oasen",
			"description" : "Konferensrum för ca 50 pers."
		}]
		"""))

        mongoTemplate.getCollection("permissions").insert(JSON.parse("""
		[{
			"_id" : "1",
			"everyone" : true,
			"patterns" : ["read:events", "read:users", "read:groups", "read:groupMemberships", "read:userResourceTypes"]
		},{
			"_id" : "2",
			"userId" : "1",
			"userFullName" : "Lars Arvidsson",
			"patterns" : ["*"]
		},{
			"_id" : "3",
			"groupId" : "2",
			"groupName" : "Mötesledare",
			"patterns" : ["update:events:*:userResources:1"]
		},{
			"_id" : "4",
			"groupId" : "3",
			"groupName" : "Tolkar",
			"patterns" : ["update:events:*:userResources:2"]
		},{
			"_id" : "5",
			"groupId" : "4",
			"groupName" : "Ljudtekniker",
			"patterns" : ["update:events:*:userResources:3"]
		}]
		"""));

        String today = RosetteDateConverter.dateToString(new Date());

        mongoTemplate.getCollection("events").insert(JSON.parse("""
		[{
			"_id" : "1",
			"title" : "Gudstjänst",
			"startTime" : ${TestUtil.mongoDate(today + " 11:00 Europe/Stockholm")},
			"description" : "Dopgudstjänst",
			"endTime" : null,
			"requiredUserResourceTypes" : ["1", "2", "4"],
			"userResources" :
			    [{
			        "userResourceTypeId" : "1",
			        "userResourceTypeName" : "Mötesledare",
			        "userReferences" :
			            [{
			                "userId" : "2",
			                "userFullName" : "Steve Jobs"
                        }]
			    },{
			        "userResourceTypeId" : "2",
			        "userResourceTypeName" : "Tolkar",
			        "userReferences" :
			            [{
			                "userId" : "3",
			                "userFullName" : "Jerry Seinfeld"
                        }]
			    },{
			        "userResourceTypeId" : "4",
			        "userResourceTypeName" : "Ljudtekniker",
			        "userReferences" :
			            [{
			                "userId" : "1",
			                "userFullName" : "Lars Arvidsson"
                        }]
			    }]
		},
		{
			"_id" : "2",
			"title" : "Bön",
			"startTime" : ${TestUtil.mongoDate(today + " 17:00 Europe/Stockholm")},
			"description" : "Bön för Sverige",
			"endTime" : null
		}]
		"""))
    }
}
