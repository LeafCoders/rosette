package se.leafcoders.rosette.integration

import org.apache.http.client.ClientProtocolException
import org.junit.Test

class SeedTest extends AbstractIntegrationTest {

    @Test
    public void test() throws ClientProtocolException, IOException {
		givenUser(user1)
		givenUser(user2)
		givenGroup(group1)
		givenGroup(group2)
		givenGroupMembership(user1, group1)
		givenGroupMembership(user2, group1)
		givenGroupMembership(user2, group2)
		givenPermissionForUser(user1, ["*"])
		givenPermissionForGroup(group2, ["*"])
		givenPermissionForEveryone(["events:read", "public:read"])

		givenLocation(location1)
		givenLocation(location2)

		givenBooking(booking1)
		givenBooking(booking2)

		givenUploadFolder(uploadFolderPosters)
		givenUploadFolder(uploadFolderLocations)
		def uploadItem1 = givenUploadInFolder("posters", validPNGImage)
		def uploadItem2 = givenUploadInFolder("posters", validJPEGImage)
		givenPoster(poster1, uploadItem1)
		givenPoster(poster2, uploadItem2)

		givenResourceType(userResourceTypeSingle)
		givenResourceType(userResourceTypeMultiAndText)
		givenResourceType(uploadResourceTypeSingle)
		givenResourceType(uploadResourceTypeMulti)

		givenEventType(eventType1)
		givenEventType(eventType2)

        givenEducationType(educationType1)
        givenEducationType(educationType2)

        givenUploadFolder(uploadFolderEducationThemes)
        def educationThemeImage = givenUploadInFolder("educationThemes", validPNGImage)
        givenEducationTheme(educationTheme1, educationThemeImage)
        givenEducationTheme(educationTheme2, educationThemeImage)

        givenEducation(eventEducation1)
        givenEducation(eventEducation2)
    }
}
