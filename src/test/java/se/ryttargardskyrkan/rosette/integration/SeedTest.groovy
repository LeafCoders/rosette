package se.ryttargardskyrkan.rosette.integration

import org.apache.http.client.ClientProtocolException
import org.junit.Test

class SeedTest extends AbstractIntegrationTest {

    @Test
    public void test() throws ClientProtocolException, IOException {
		givenUser(user1)
		givenUser(user2)
		givenGroup(group1)
		givenGroup(group2)
		givenGroupMembership(user2, group2)
		givenPermissionForUser(user1, ["*"])
		givenPermissionForGroup(group2, ["*"])
		givenPermissionForEveryone(["read:events"])

		givenLocation(location1)
		givenLocation(location2)

		givenBooking(booking1)
		givenBooking(booking2)

		def uploadItem = givenUploadInFolder("posters", validPNGImage)
		givenPoster(poster1, uploadItem)
		givenPoster(poster2, uploadItem)

		givenResourceType(userResourceType1)
		givenResourceType(uploadResourceType1)

		givenEventType(eventType1)
		givenEventType(eventType2)
    }
}
