
package se.leafcoders.rosette.integration

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import javax.servlet.http.HttpServletResponse
import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpDelete
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpPut
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClientBuilder
import org.bson.types.ObjectId
import org.junit.After
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.gridfs.GridFsTemplate
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import se.leafcoders.rosette.integration.util.TestUtil
import se.leafcoders.rosette.model.*
import se.leafcoders.rosette.model.education.Education
import se.leafcoders.rosette.model.education.EducationType
import se.leafcoders.rosette.model.education.EventEducation
import se.leafcoders.rosette.model.education.EventEducationType
import se.leafcoders.rosette.model.event.Event
import se.leafcoders.rosette.model.reference.EducationTypeRef
import se.leafcoders.rosette.model.reference.EventRef
import se.leafcoders.rosette.model.reference.LocationRefOrText
import se.leafcoders.rosette.model.reference.ObjectReferences
import se.leafcoders.rosette.model.reference.UploadResponseRefs
import se.leafcoders.rosette.model.reference.UserRef
import se.leafcoders.rosette.model.reference.UserRefsAndText
import se.leafcoders.rosette.model.resource.*
import se.leafcoders.rosette.model.upload.UploadFolder
import se.leafcoders.rosette.model.upload.UploadFolderRef
import se.leafcoders.rosette.model.upload.UploadRequest
import se.leafcoders.rosette.model.upload.UploadResponse
import se.leafcoders.rosette.util.QueryId
import com.fasterxml.jackson.databind.ObjectMapper
import com.mongodb.Mongo
import com.mongodb.MongoException
import com.mongodb.WriteConcern
import com.mongodb.util.JSON

abstract class AbstractIntegrationTest {
	protected static MongoTemplate mongoTemplate
	protected static GridFsTemplate gridFsTemplate
	protected static HttpClient httpClient
	protected static ObjectMapper mapper

	protected static String baseUrl = "http://localhost:9000/api/v1"

	private HttpPost postRequest = null;
	private HttpGet getRequest = null;
	private HttpPut putRequest = null;
	private HttpDelete deleteRequest = null;
	
	@BeforeClass
	static void beforeClass() throws UnknownHostException, MongoException {
		mongoTemplate = new MongoTemplate(new Mongo(), "rosette-test")
		mongoTemplate.setWriteConcern(WriteConcern.ACKNOWLEDGED)
		gridFsTemplate = new GridFsTemplate(mongoTemplate.mongoDbFactory, mongoTemplate.converter)
		mapper = new ObjectMapper()
	}
	
	@Before
	public void before() {
		// Clearing auth cache
		httpClient = HttpClientBuilder.create().build()
		resetAuthCaches()
		httpClient.getConnectionManager().shutdown()
		httpClient = HttpClientBuilder.create().build()

		mongoTemplate.dropCollection("signupUsers")
		mongoTemplate.dropCollection("users")
		mongoTemplate.dropCollection("groups")
		mongoTemplate.dropCollection("groupMemberships")
        mongoTemplate.dropCollection("educations")
        mongoTemplate.dropCollection("educationTypes")
		mongoTemplate.dropCollection("events")
        mongoTemplate.dropCollection("eventTypes")
		mongoTemplate.dropCollection("permissions")
        mongoTemplate.dropCollection("permissionTrees")
        mongoTemplate.dropCollection("posters")
        mongoTemplate.dropCollection("resourceTypes")
        mongoTemplate.dropCollection("userResourceTypes")
        mongoTemplate.dropCollection("locations")
        mongoTemplate.dropCollection("bookings")
        mongoTemplate.dropCollection("uploadFolders")
        gridFsTemplate.delete(null)
	}

	@After
	public void after() {
		releasePostRequest()
		releaseGetRequest()
		releasePutRequest()
		releaseDeleteRequest()
	}

	@AfterClass
	static void afterClass() {
		gridFsTemplate = null
		mongoTemplate = null
		
		httpClient.getConnectionManager().shutdown()
		httpClient = null
		
		mapper = null		
	}

	void releasePostRequest() {
		if (postRequest != null) {
			postRequest.releaseConnection()
			postRequest = null
		}
	}
	void releaseGetRequest() {
		if (getRequest != null) {
			getRequest.releaseConnection()
			getRequest = null
		}
	}
	void releasePutRequest() {
		if (putRequest != null) {
			putRequest.releaseConnection()
			putRequest = null
		}
	}
	void releaseDeleteRequest() {
		if (deleteRequest != null) {
			deleteRequest.releaseConnection()
			deleteRequest = null
		}
	}

	void resetAuthCaches() {
		HttpDelete httpDelete = new HttpDelete(baseUrl + "/groupMemberships/notIValidId")
		httpClient.execute(httpDelete)
		httpDelete.releaseConnection()
	}

	/*
	 *  Helper data and methods
	 */
	public String getObjectId() { return new ObjectId().toString() }

	private boolean hasAddedUploadUser = false
	private void createTestUploadUser() {
		if (!hasAddedUploadUser) {
			hasAddedUploadUser = true
			givenUser(userTestUpload)
			givenPermissionForUser(userTestUpload, ["uploads:*"])
			System.sleep(100);
		}
	}

	/*
	 *  Data objects
	 */

	private final hashedPassword = new BCryptPasswordEncoder().encode("password")
    private final String userTestUploadId = getObjectId()
	private final User userTestUpload = new User(
		id : userTestUploadId,
		email : "user@testupload.com",
		firstName : "User",
		lastName : "Test Upload",
		hashedPassword : "${hashedPassword}"
	)

    private final String user1Id = getObjectId()
	protected final User user1 = new User(
		id : user1Id,
		email : "u1@ser.se",
		firstName : "User",
		lastName : "One",
		hashedPassword : "${hashedPassword}"
	)
	protected final UserRef userRef1 = new UserRef(user1)

    private final String user2Id = getObjectId()
	protected final User user2 = new User(
		id : user2Id,
		email : "u2@ser.se",
		firstName : "User",
		lastName : "Two",
		hashedPassword : "${hashedPassword}"
	)
	protected final UserRef userRef2 = new UserRef(user2)

    private final String signupUser1Id = getObjectId()
	protected final SignupUser signupUser1 = new SignupUser(
		id : signupUser1Id,
		email : "u1@sign.se",
		firstName : "User",
		lastName : "One",
		permissions : "Perms for u1",
		hashedPassword : "${hashedPassword}"
	)
    
	protected final Group group1 = new Group(
		id : "adminsGroup",
		name : "Admins"
	)
	protected final Group group2 = new Group(
		id : "usersGroup",
		name : "Users"
	)

	protected final Location location1 = new Location(
		id : getObjectId(),
		name : "Away",
		description : "Description...",
		directionImage : null
	)
	protected final Location location2 = new Location(
		id : getObjectId(),
		name : "Home",
		description : "Description...",
		directionImage : null
	)

	protected final Booking booking1 = new Booking(
		id : getObjectId(),
		customerName : "Scan",
		startTime : TestUtil.modelDate("2012-03-25 11:00 Europe/Stockholm"),
		endTime : TestUtil.modelDate("2012-03-26 11:00 Europe/Stockholm"),
		location : new LocationRefOrText(ref: location1)
	)
	protected final Booking booking2 = new Booking(
		id : getObjectId(),
		customerName : "Arla",
		startTime : TestUtil.modelDate("2014-01-21 11:00 Europe/Stockholm"),
		endTime : TestUtil.modelDate("2014-01-22 12:00 Europe/Stockholm"),
		location : new LocationRefOrText(text: "A location")
	)

	protected final Poster poster1 = new Poster(
		id : getObjectId(),
		title : "Poster1 title",
		startTime : TestUtil.modelDate("2012-03-25 11:00 Europe/Stockholm"),
		endTime : TestUtil.modelDate("2012-03-26 11:00 Europe/Stockholm"),
		duration : 15,
        image : null
	)
	protected final Poster poster2 = new Poster(
		id : getObjectId(),
		title : "Poster2 title",
		startTime : TestUtil.modelDate("2012-03-26 10:00 Europe/Stockholm"),
		endTime : TestUtil.modelDate("2012-03-27 18:00 Europe/Stockholm"),
		duration : 10,
        image : null
	)

	protected final UploadFolder uploadFolderPosters = new UploadFolder(
		id: "posters",
		name: "Posters",
		isPublic: true,
		mimeTypes: ["image/"]
	)
	protected final UploadFolderRef uploadFolderPostersRef = new UploadFolderRef(uploadFolderPosters)

	protected final UploadFolder uploadFolderLocations = new UploadFolder(
		id: "locations",
		name: "Locations",
		isPublic: true,
		mimeTypes: ["image/"]
	)
	protected final UploadFolderRef uploadFolderLocationsRef = new UploadFolderRef(uploadFolderLocations)

	protected final UploadRequest validPNGImage = new UploadRequest(
        fileName : "image.png",
        mimeType : "image/png",
        fileData : "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAABAWlDQ1BJQ0MgUHJvZmlsZQAAGBljYGC8k1hQkMPEwMCQm1dSFOTupBARGaUA5MJBYnJxgWNAgA9IIC8/LxUuAWd8u8bACOJc1nV2D1ao2TrN8VKr2JnJE+fttOuadAGuDDuDo7ykoAQo9QSIRYqAlgPpHyB2OpjNyANiJ0HYCiB2UUiQMwMDowmQzZcOYbuA2EkQdgiInZJanAxUkwJklyH88zkE7E5GsZMIsfwFDAyW8gwMzN0IsaRpDAzb9zMwSJxBiKkA1fHbMDBsO5dcWlQGNBcEGBnPMjAQ4kPcAlYv456al1qUmawQUJRZlliSqgAK74Ci/LTMHCzBCtZCPgEAA1NJWOMhui0AAAAJcEhZcwAACxMAAAsTAQCanBgAAAI9aVRYdFhNTDpjb20uYWRvYmUueG1wAAAAAAA8eDp4bXBtZXRhIHhtbG5zOng9ImFkb2JlOm5zOm1ldGEvIiB4OnhtcHRrPSJYTVAgQ29yZSA1LjQuMCI+CiAgIDxyZGY6UkRGIHhtbG5zOnJkZj0iaHR0cDovL3d3dy53My5vcmcvMTk5OS8wMi8yMi1yZGYtc3ludGF4LW5zIyI+CiAgICAgIDxyZGY6RGVzY3JpcHRpb24gcmRmOmFib3V0PSIiCiAgICAgICAgICAgIHhtbG5zOnRpZmY9Imh0dHA6Ly9ucy5hZG9iZS5jb20vdGlmZi8xLjAvIj4KICAgICAgICAgPHRpZmY6WFJlc29sdXRpb24+NzI8L3RpZmY6WFJlc29sdXRpb24+CiAgICAgICAgIDx0aWZmOlJlc29sdXRpb25Vbml0PjI8L3RpZmY6UmVzb2x1dGlvblVuaXQ+CiAgICAgICAgIDx0aWZmOllSZXNvbHV0aW9uPjcyPC90aWZmOllSZXNvbHV0aW9uPgogICAgICAgICA8dGlmZjpPcmllbnRhdGlvbj4xPC90aWZmOk9yaWVudGF0aW9uPgogICAgICAgICA8dGlmZjpQaG90b21ldHJpY0ludGVycHJldGF0aW9uPjI8L3RpZmY6UGhvdG9tZXRyaWNJbnRlcnByZXRhdGlvbj4KICAgICAgPC9yZGY6RGVzY3JpcHRpb24+CiAgIDwvcmRmOlJERj4KPC94OnhtcG1ldGE+CjAVYfMAAABzSURBVDgR1VFBCsAwCFvH3lrf5Gs3OgjEOEehh7FeotXEiK33fm4Lb1/g3tTvBY5qBXcPJTMLOZLXFQapIkKg6RV0MhqBKhgcKPnJgfYEgTEFE4D8BxeMSYCLM3EpwFY5VtF0Rm7mWInI0xVQmMVyhf8IXIo8H+rM3fTBAAAAAElFTkSuQmCC"
    )
	protected final UploadRequest validJPEGImage = new UploadRequest(
        fileName : "image.jpg",
        mimeType : "image/jpg",
        fileData : "data:image/jpg;base64,/9j/4AAQSkZJRgABAQEASABIAAD/4gHsSUNDX1BST0ZJTEUAAQEAAAHcYXBwbAIAAABtbnRyUkdCIFhZWiAAAAAAAAAAAAAAAABhY3NwQVBQTAAAAABub25lAAAAAAAAAAAAAAAAAAAAAAAA9tYAAQAAAADTLUNHUyB8tZZB0oUWzJORnrk+ipLQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAh3dHB0AAAA5AAAABRyWFlaAAAA+AAAABRnWFlaAAABDAAAABRiWFlaAAABIAAAABRyVFJDAAABNAAAAA5nVFJDAAABRAAAAA5iVFJDAAABVAAAAA5kZXNjAAABZAAAAHZYWVogAAAAAAAA81QAAQAAAAEWyVhZWiAAAAAAAABvoAAAOR8AAAOLWFlaIAAAAAAAAGKWAAC3vwAAGMxYWVogAAAAAAAAJKAAAA88AAC2zmN1cnYAAAAAAAAAAQHNAABjdXJ2AAAAAAAAAAEBzQAAY3VydgAAAAAAAAABAc0AAGRlc2MAAAAAAAAAHEdlbmVyaWMgUHJpdmF0ZSBSR0IgUHJvZmlsZQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAP/hAIxFeGlmAABNTQAqAAAACAAGAQYAAwAAAAEAAgAAARIAAwAAAAEAAQAAARoABQAAAAEAAABWARsABQAAAAEAAABeASgAAwAAAAEAAgAAh2kABAAAAAEAAABmAAAAAAAAAEgAAAABAAAASAAAAAEAAqACAAQAAAABAAAAEKADAAQAAAABAAAAEAAAAAD/2wBDAAIBAQIBAQICAQICAgICAwUDAwMDAwYEBAMFBwYHBwcGBgYHCAsJBwgKCAYGCQ0JCgsLDAwMBwkNDg0MDgsMDAv/2wBDAQICAgMCAwUDAwULCAYICwsLCwsLCwsLCwsLCwsLCwsLCwsLCwsLCwsLCwsLCwsLCwsLCwsLCwsLCwsLCwsLCwv/wAARCAAQABADASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwBf2Tf2IfFP7Usera1aw3Wk+BPDcNzLrPiJhbCGzaK1knEaC6ubeORmKRqx81ViEyPIyIQSftZfsQ+Kf2Wo9J1q6hutW8CeJIbaXRvESi2MN40trHOY3Frc3EcbKXkVT5rLKIXeNnQEi18C/wBqrwp8O/gNrPgT4p+A9T8V2ur/ANop9otPEY0vyEuzpUm4J9klJlim0W2kRi2w7mV43Fbn7Qn7dWg/Fb9jvwh8G/hn8Prrwnovg/U01KC8ufEJ1Oa5YR3Ik8wfZogGkku3lJUhQcqqKuAoB//Z"
    )

	protected final UserResourceType userResourceTypeSingle = new UserResourceType(
		type : 'user',
		id : "speaker",
		section : "persons",
		name : "UserResourceType Single",
		description : "Description here",
		multiSelect : false,
		allowText : false,
		group : group1
	)
	protected final UserResourceType userResourceTypeMultiAndText = new UserResourceType(
		type : 'user',
		id : "readers",
		section : "persons",
		name : "UserResourceType Multi",
		description : "Description here",
		multiSelect : true,
		allowText : true,
		group : group1
	)
	protected final UploadResourceType uploadResourceTypeSingle = new UploadResourceType(
		type : 'upload',
		id : "posterFile",
		section : "files",
		name : "UploadResourceType Single",
		description : "A poster file",
		multiSelect : false,
		uploadFolder : uploadFolderPostersRef
	)
	protected final UploadResourceType uploadResourceTypeMulti = new UploadResourceType(
		type : 'upload',
		id : "posterFiles",
		section : "files",
		name : "UploadResourceType Multi",
		description : "Some poster files",
		multiSelect : true,
		uploadFolder : uploadFolderPostersRef
	)

	protected final EventType eventType1 = new EventType(
		id : "people",
		name : "EventType 1",
		description : "Description...",
		hasPublicEvents : new DefaultSetting<Boolean>(value: true, allowChange: true),
		resourceTypes : [ userResourceTypeSingle, uploadResourceTypeSingle ]
	)
	protected final EventType eventType2 = new EventType(
		id : "groups",
		name : "EventType 2",
		description : "Description...",
		hasPublicEvents : new DefaultSetting<Boolean>(value: false, allowChange: false),
		resourceTypes : [ userResourceTypeMultiAndText, uploadResourceTypeMulti ]
	)

	protected final Event event1 = new Event(
		id : getObjectId(),
		eventType : eventType1,
		title : "An event",
		startTime : TestUtil.modelDate("2012-03-26 11:00 Europe/Stockholm"),
		endTime : TestUtil.modelDate("2012-03-26 12:00 Europe/Stockholm"),
		description : "Description...",
		location : new LocationRefOrText(ref: location1),
		isPublic : true,
		resources : [
			new UserResource(
				type : "user",
				resourceType : userResourceTypeSingle, 
				users : new UserRefsAndText(refs: [ userRef1 ] as ObjectReferences<UserRef>)
			),
			new UploadResource(
				type : "upload",
				resourceType : uploadResourceTypeSingle, 
				uploads : new UploadResponseRefs()
			)
		]
	)
	protected final Event event2 = new Event(
		id : getObjectId(),
		eventType : eventType1,
		title : "Another event",
		startTime : TestUtil.modelDate("2014-10-05 18:30 Europe/Stockholm"),
		endTime : TestUtil.modelDate("2014-10-05 20:00 Europe/Stockholm"),
		description : null,
		location : null,
		isPublic: false,
		resources : [
			new UserResource(
				type : "user",
				resourceType : userResourceTypeMultiAndText, 
				users : new UserRefsAndText(refs: [ userRef1 ] as ObjectReferences<UserRef>)
			),
			new UploadResource(
				type : "upload",
				resourceType : uploadResourceTypeMulti, 
				uploads : new UploadResponseRefs()
			)
		]
	)
	protected final Event event3 = new Event(
		id : getObjectId(),
		eventType : eventType2,
		title : "Some event",
		startTime : TestUtil.modelDate("2015-01-01 08:30 Europe/Stockholm"),
		endTime : TestUtil.modelDate("2015-01-01 10:00 Europe/Stockholm"),
		description : null,
		location : null,
		isPublic: false,
		resources : []
	)
    protected final EventRef eventRef1 = new EventRef(event1)
    protected final EventRef eventRef2 = new EventRef(event2)
    protected final EventRef eventRef3 = new EventRef(event3)
    
    protected final EventEducationType eventEducationType1 = new EventEducationType(
        type : 'event',
        id : "letters",
        name : "Letters",
        description : "Letters about life",
        authorResourceType : userResourceTypeSingle,
        eventType : eventType1
    )
    protected final EventEducationType eventEducationType2 = new EventEducationType(
        type : 'event',
        id : "letters2",
        name : "Letters2",
        description : "Letters (2) about life",
        authorResourceType : userResourceTypeSingle,
        eventType : eventType1
    )
    protected final EducationTypeRef eventEducationTypeRef1 = new EducationTypeRef(eventEducationType1)
    protected final EducationTypeRef eventEducationTypeRef2 = new EducationTypeRef(eventEducationType2)
    
    protected final EventEducation eventEducation1 = new EventEducation(
        type : 'event',
        educationType : eventEducationTypeRef1,
        id : getObjectId(),
        title : "Education1",
        content : "Education1 content",
        questions : "Education1 questions",
        event : eventRef1
    )
    protected final EventEducation eventEducation2 = new EventEducation(
        type : 'event',
        educationType : eventEducationTypeRef2,
        id : getObjectId(),
        title : "Education2",
        content : "Education2 content",
        questions : "Education2 questions",
        event : eventRef2
    )

	/*
	 *  Given
	 */
	protected void givenUser(User user) {
		mongoTemplate.insert(user);
	}

	protected void givenSignupUser(SignupUser signupUser) {
		mongoTemplate.insert(signupUser);
	}

	protected String givenPermissionForEveryone(List<String> permissions) {
		String permissionId = getObjectId()
		mongoTemplate.insert(new Permission(
			id : permissionId,
			everyone : true,
			patterns : permissions.collect { it.toString() } // Convert GString to String
		))
		return permissionId
	}
	protected String givenPermissionForUser(User user, List<String> permissions) {
		String permissionId = getObjectId()
		mongoTemplate.insert(new Permission(
			id : permissionId,
			user : new UserRef(user),
			patterns : permissions.collect { it.toString() } // Convert GString to String
		))
		return permissionId
	}
	protected String givenPermissionForGroup(Group group, List<String> permissions) {
		String permissionId = getObjectId()
		mongoTemplate.insert(new Permission(
			id : permissionId,
			group : group,
			patterns : permissions.collect { it.toString() } // Convert GString to String
		))
		return permissionId
	}

	protected void givenGroup(Group group) {
		mongoTemplate.insert(group);
	}

	protected String givenGroupMembership(User user, Group group) {
		String groupMembershipId = getObjectId()
		mongoTemplate.insert(new GroupMembership(
			id : groupMembershipId,
			user : new UserRef(user),
			group : group
		))
		return groupMembershipId
	}

    protected void givenEducationType(EducationType educationType) {
        mongoTemplate.insert(educationType)
    }

    protected void givenEducation(Education education) {
        mongoTemplate.insert(education)
    }

	protected void givenResourceType(ResourceType resourceType) {
		mongoTemplate.insert(resourceType)
	}

	protected void givenEventType(EventType eventType) {
		mongoTemplate.insert(eventType)
	}

	protected void givenEvent(Event event) {
		mongoTemplate.insert(event)
	}
	
	protected void givenLocation(Location location) {
		mongoTemplate.insert(location)
	}

	protected void givenBooking(Booking booking) {
		mongoTemplate.insert(booking)
	}

	protected void givenPoster(Poster poster, UploadResponse upload) {
		poster.image = upload
		mongoTemplate.insert(poster)
	}
	
	protected void givenUploadFolder(UploadFolder uploadFolder) {
		mongoTemplate.insert(uploadFolder)
	}
	
	protected UploadResponse givenUploadInFolder(String folderId, UploadRequest upload) {
		createTestUploadUser()
        String postUrl = "/uploads/" + folderId
		String postContent = mapper.writeValueAsString(upload)
		HttpResponse postResponse = whenPost(postUrl, userTestUpload, postContent)
		String responseBody = thenResponseCodeIs(postResponse, HttpServletResponse.SC_CREATED)
		UploadResponse responseObj = (UploadResponse) JSON.parse(responseBody)
		releasePostRequest()
		return responseObj 
	}



	/*
	 *  When
	 */
	protected HttpResponse whenPost(String postUrl, User user, String requestBody) {
        postRequest = new HttpPost(baseUrl + postUrl)
		postRequest.setEntity(new StringEntity(requestBody, "application/json", "UTF-8"))
		if (user != null) {
            postRequest.addHeader("X-AUTH-TOKEN", xAuthToken(user.id))
		}
		HttpResponse resp = httpClient.execute(postRequest)
		return resp
	}

	protected HttpResponse whenGet(String getUrl, User user = null, boolean relativeUrl = true) {
        getRequest = new HttpGet(relativeUrl ? (baseUrl + getUrl) : getUrl)
		getRequest.addHeader("Accept", "application/json; charset=UTF-8")
		getRequest.addHeader("Content-Type", "application/json; charset=UTF-8")
		if (user != null) {
            getRequest.addHeader("X-AUTH-TOKEN", xAuthToken(user.id))
		}
		HttpResponse resp = httpClient.execute(getRequest)
		return resp
	}

	protected HttpResponse whenPut(String putUrl, User user, String requestBody) {
        putRequest = new HttpPut(baseUrl + putUrl)
		putRequest.setEntity(new StringEntity(requestBody, "application/json", "UTF-8"))
		if (user != null) {
            putRequest.addHeader("X-AUTH-TOKEN", xAuthToken(user.id))
		}
		HttpResponse resp = httpClient.execute(putRequest)
		return resp
	}

	protected HttpResponse whenDelete(String deleteUrl, User user) {
        deleteRequest = new HttpDelete(baseUrl + deleteUrl)
		deleteRequest.addHeader("Accept", "application/json; charset=UTF-8")
		if (user != null) {
            deleteRequest.addHeader("X-AUTH-TOKEN", xAuthToken(user.id))
		}
		HttpResponse resp = httpClient.execute(deleteRequest)
		return resp
	}

    private String xAuthToken(String userId) {
        final String jwtSecret = "tooManySecrets"
        return Jwts.builder().setSubject(userId).signWith(SignatureAlgorithm.HS512, jwtSecret).compact()
    }

	/*
	 *  Then
	 */
	protected String thenResponseCodeIs(HttpResponse response, int code) {
		String responseBody = TestUtil.jsonFromResponse(response)
		assertEquals(responseBody + " - ", code, response.getStatusLine().getStatusCode())
		return responseBody
	}

	protected void thenResponseHeaderHas(HttpResponse response, String type, String value) {
		assertEquals(value, response.getHeaders(type)[0].getValue())
	}

	protected void thenResponseDataIs(String responseBody, String expectedBody) {
		TestUtil.assertJsonEquals(expectedBody, responseBody)
	}

	protected void thenDataInDatabaseIs(Class entityClass, String expectedBody) {
		List<Object> inDatabase = mongoTemplate.findAll(entityClass)
		TestUtil.assertJsonEquals(expectedBody, mapper.writeValueAsString(inDatabase))
	}

	protected void thenDataInDatabaseIs(Class entityClass, String itemId, Closure findSubContent, String expectedBody) {
		Query findOneQuery = Query.query(Criteria.where("id").is(QueryId.get(itemId)))
		Object inDatabase = mongoTemplate.findOne(findOneQuery, entityClass)
		Object subContent = findSubContent.call(inDatabase)
		TestUtil.assertJsonEquals(expectedBody, mapper.writeValueAsString(subContent))
	}

	protected void thenItemsInDatabaseIs(Class entityClass, Long count) {
		assertEquals(count, mongoTemplate.count(new Query(), entityClass))
	}

	protected void thenAssetWithNameExist(String fileName, String fileUrl) {
		createTestUploadUser()
		HttpResponse assetsResponse = whenGet(fileUrl, userTestUpload, false)
		assertEquals(HttpServletResponse.SC_OK, assetsResponse.getStatusLine().getStatusCode())
		assertTrue(assetsResponse.allHeaders.toString().contains(fileName))
		releaseGetRequest()
	}

	protected void thenAssetDontExist(String fileUrl) {
		createTestUploadUser()
		HttpResponse assetsResponse = whenGet(fileUrl, userTestUpload, false)
		assertEquals(HttpServletResponse.SC_NOT_FOUND, assetsResponse.getStatusLine().getStatusCode())
		releaseGetRequest()
	}
	
	protected String toJSON(Object data) {
		return mapper.writeValueAsString(data);
	}
}
