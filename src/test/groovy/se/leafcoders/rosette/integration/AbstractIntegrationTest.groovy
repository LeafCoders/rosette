package se.leafcoders.rosette.integration

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import javax.servlet.http.HttpServletResponse
import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpDelete
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpPut
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.entity.mime.MultipartEntityBuilder
import org.apache.http.impl.client.HttpClientBuilder
import org.bson.types.ObjectId
import org.junit.After
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import se.leafcoders.rosette.integration.util.TestUtil
import se.leafcoders.rosette.model.*
import se.leafcoders.rosette.model.education.Education
import se.leafcoders.rosette.model.education.EducationTheme
import se.leafcoders.rosette.model.education.EducationThemeRef
import se.leafcoders.rosette.model.education.EducationType
import se.leafcoders.rosette.model.education.EducationTypeRef
import se.leafcoders.rosette.model.education.EventEducation
import se.leafcoders.rosette.model.education.SimpleEducation
import se.leafcoders.rosette.model.event.Event
import se.leafcoders.rosette.model.podcast.Podcast
import se.leafcoders.rosette.model.reference.EventRef
import se.leafcoders.rosette.model.reference.LocationRefOrText
import se.leafcoders.rosette.model.reference.ObjectReferences
import se.leafcoders.rosette.model.reference.UploadFileRefs
import se.leafcoders.rosette.model.reference.UserRef
import se.leafcoders.rosette.model.reference.UserRefOrText
import se.leafcoders.rosette.model.reference.UserRefsAndText
import se.leafcoders.rosette.model.resource.*
import se.leafcoders.rosette.model.upload.UploadFile
import se.leafcoders.rosette.model.upload.UploadFolder
import se.leafcoders.rosette.model.upload.UploadFolderRef
import se.leafcoders.rosette.model.upload.UploadRequest
import se.leafcoders.rosette.util.QueryId
import com.fasterxml.jackson.databind.ObjectMapper
import com.mongodb.Mongo
import com.mongodb.MongoException
import com.mongodb.WriteConcern
import com.mongodb.util.JSON

abstract class AbstractIntegrationTest {
	protected static MongoTemplate mongoTemplate
	protected static HttpClient httpClient
	protected static ObjectMapper mapper

    protected static String baseUrl = System.getProperty("rosette.baseUrl", "http://localhost:9000");
    protected static String baseAuthUrl = baseUrl + "/auth";
    protected static String baseApiUrl = baseUrl + "/api/v1";

	private HttpPost postRequest = null;
	private HttpGet getRequest = null;
	private HttpPut putRequest = null;
	private HttpDelete deleteRequest = null;
	
	@BeforeClass
	static void beforeClass() throws UnknownHostException, MongoException {
		mongoTemplate = new MongoTemplate(new Mongo(), "rosette-test")
		mongoTemplate.setWriteConcern(WriteConcern.ACKNOWLEDGED)
		mapper = new ObjectMapper()
	}
	
	@Before
	public void before() {
		// Clearing auth cache
		httpClient = HttpClientBuilder.create().build()
		resetAuthCaches()
        deleteAllUploads()
		httpClient.getConnectionManager().shutdown()
		httpClient = HttpClientBuilder.create().build()

		mongoTemplate.dropCollection("bookings")
		mongoTemplate.dropCollection("educations")
		mongoTemplate.dropCollection("educationThemes")
		mongoTemplate.dropCollection("educationTypes")
		mongoTemplate.dropCollection("events")
		mongoTemplate.dropCollection("eventTypes")
        mongoTemplate.dropCollection("forgottenPasswords")
		mongoTemplate.dropCollection("groups")
		mongoTemplate.dropCollection("groupMemberships")
		mongoTemplate.dropCollection("locations")
		mongoTemplate.dropCollection("permissions")
		mongoTemplate.dropCollection("permissionTrees")
		mongoTemplate.dropCollection("podcasts")
		mongoTemplate.dropCollection("posters")
		mongoTemplate.dropCollection("resourceTypes")
		mongoTemplate.dropCollection("signupUsers")
		mongoTemplate.dropCollection("textValues")
        mongoTemplate.dropCollection("uploadFiles")
        mongoTemplate.dropCollection("uploadFolders")
		mongoTemplate.dropCollection("userResourceTypes")
		mongoTemplate.dropCollection("users")
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
        mapper = null       
		mongoTemplate = null
		httpClient.getConnectionManager().shutdown()
		httpClient = null
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
		HttpDelete httpDelete = new HttpDelete(baseApiUrl + "/development/resetPermissionCache")
		httpClient.execute(httpDelete)
		httpDelete.releaseConnection()
	}

    private void deleteAllUploads() {
        HttpDelete httpDelete = new HttpDelete(baseApiUrl + "/development/deleteAllUploads")
        httpClient.execute(httpDelete)
        httpDelete.releaseConnection()
    }

	/*
	 *  Helper data and methods
	 */
	public String getObjectId() { return new ObjectId().toString() }

	private boolean hasAddedUploadUser = false

	public void createTestUploadUser() {
		if (!hasAddedUploadUser) {
			hasAddedUploadUser = true
			givenUser(userTestUpload)
			givenPermissionForUser(userTestUpload, ["uploads:*", "uploadFolders:*"])
			System.sleep(100);
		}
	}

	/*
	 *  Data objects
	 */

	public final hashedPassword = new BCryptPasswordEncoder().encode("password")
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

    protected final UploadFolder uploadFolderEducationThemes = new UploadFolder(
        id: "educationThemes",
        name: "EducationThemes",
        isPublic: true,
        mimeTypes: ["image/"]
    )
    protected final UploadFolderRef uploadFolderEducationThemesRef = new UploadFolderRef(uploadFolderEducationThemes)

    protected final UploadFolder uploadFolderPodcastImages = new UploadFolder(
        id: "podcastImages",
        name: "Podcast images",
        isPublic: true,
        mimeTypes: ["image/"]
    )
    protected final UploadFolderRef uploadFolderPodcastImagesRef = new UploadFolderRef(uploadFolderPodcastImages)

    protected final UploadFolder uploadFolderEducations = new UploadFolder(
        id: "educations",
        name: "Educations",
        isPublic: true,
        mimeTypes: ["audio/"]
    )
    protected final UploadFolderRef uploadFolderEducationsRef = new UploadFolderRef(uploadFolderEducations)

	protected final UploadRequest validPNGImage = new UploadRequest(
        fileName : "image.png",
        mimeType : "image/png"
    )
	protected final UploadRequest validJPEGImage = new UploadRequest(
        fileName : "image.jpg",
        mimeType : "image/jpg"
    )
    protected final UploadRequest audioRecording1 = new UploadRequest(
        fileName: "audio.mp3",
        mimeType: "audio/mp3"
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
		description : "Event description.\n{SingleUser: #speaker}",
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
				uploads : new UploadFileRefs()
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
				uploads : new UploadFileRefs()
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
    
    protected final EducationType educationType1 = new EducationType(
        id : "letters",
        name : "Letters",
        description : "Letters about life",
        authorResourceType : userResourceTypeSingle,
        eventType : eventType1,
        uploadFolder: uploadFolderEducations
    )
    protected final EducationType educationType2 = new EducationType(
        id : "letters2",
        name : "Letters2",
        description : "Letters (2) about life",
        authorResourceType : userResourceTypeSingle,
        eventType : eventType1,
        uploadFolder: uploadFolderEducations
    )
    protected final EducationTypeRef educationTypeRef1 = new EducationTypeRef(educationType1)
    protected final EducationTypeRef educationTypeRef2 = new EducationTypeRef(educationType2)
    
    protected final EducationTheme educationTheme1 = new EducationTheme(
        id : getObjectId(),
        educationType : educationTypeRef1,
        title : "Theme1",
        content : "The theme 1 content"
    )
    protected final EducationTheme educationTheme2 = new EducationTheme(
        id : getObjectId(),
        educationType : educationTypeRef1,
        title : "Theme2",
        content : "The theme 2 content"
    )
    protected final EducationThemeRef educationThemeRef1 = new EducationThemeRef(educationTheme1)
    protected final EducationThemeRef educationThemeRef2 = new EducationThemeRef(educationTheme2)
    
    protected final EventEducation eventEducation1 = new EventEducation(
        type : 'event',
        educationType : educationTypeRef1,
        educationTheme : educationThemeRef1,
        id : getObjectId(),
        time: event1.startTime,
        authorName : user1.fullName,
        title : "Education1",
        content : "Education1 content öäåè.",
        questions : "Education1 questions",
        recording : null,
        event : eventRef1
    )
    protected final EventEducation eventEducation2 = new EventEducation(
        type : 'event',
        educationType : educationTypeRef2,
        educationTheme : educationThemeRef1,
        id : getObjectId(),
        time: event2.startTime,
        authorName : null,
        title : "Education2",
        content : "Education2 content",
        questions : "Education2 questions",
        recording : null,
        event : eventRef2
    )
    protected final SimpleEducation simpleEducation1 = new SimpleEducation(
        type : 'simple',
        educationType : educationTypeRef1,
        educationTheme : educationThemeRef1,
        id : getObjectId(),
        time: TestUtil.modelDate("2014-10-06 12:00 Europe/Stockholm"),
        authorName : "Kalle Boll",
        title : "Education Simple 1",
        content : "Education Simple 1 content",
        questions : "Education Simple 1 questions",
        recording : null,
        author : new UserRefOrText(ref: userRef1)
    )

    protected final Podcast podcast1 = new Podcast(
        id : getObjectId(),
        educationType : educationTypeRef1,
        title : "Podcast1 title",
        subTitle : "Podcast1 sub title",
        description : "Podcast1 description",
        authorName : "Kalle Karlsson",
        copyright : "Kalle & Co",
        mainCategory : "Religion & Spirituality",
        subCategory : "Christianity",
        language : "sv-se",
        link : "http://google.se",
        changedDate : TestUtil.modelDate("2015-11-14 08:00 Europe/Stockholm"),
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

    protected void givenEducationTheme(EducationTheme educationTheme, UploadFile image) {
        educationTheme.image = image
        mongoTemplate.insert(educationTheme)
    }

    protected void givenEducation(Education education, UploadFile recording) {
        education.recording = recording
        mongoTemplate.insert(education)
    }

    protected void givenPodcast(Podcast podcast, UploadFile image) {
        podcast.image = image
        mongoTemplate.insert(podcast)
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

	protected void givenPoster(Poster poster, UploadFile image) {
		poster.image = image
		mongoTemplate.insert(poster)
	}
	
	protected void givenUploadFolder(UploadFolder uploadFolder) {
        createTestUploadUser()
        String mimeTypes = uploadFolder.mimeTypes.collect({ "\"${it}\"" }).join(',')
        String postUrl = "/uploadFolders"
        whenPost(postUrl, userTestUpload, """{
            "id": "${ uploadFolder.id }",
            "name": "${ uploadFolder.name }",
            "isPublic": ${ uploadFolder.isPublic ? 'true' : 'false' },
            "mimeTypes": [${ mimeTypes }]
        }""")
        releasePostRequest()
	}
	
	protected UploadFile givenUploadInFolder(String folderId, UploadRequest upload) {
		createTestUploadUser()
        String postUrl = "/uploads/" + folderId

        HttpResponse postResponse = whenPostUpload(postUrl, userTestUpload, upload)
        HttpEntity result = postResponse.getEntity();
        
        String responseBody = thenResponseCodeIs(postResponse, HttpServletResponse.SC_CREATED)
        UploadFile responseObj = (UploadFile) JSON.parse(responseBody)
        return responseObj
	}

    protected ForgottenPassword givenForgottenPassword(User user) {
        ForgottenPassword fp = new ForgottenPassword(
            id: getObjectId(),
            token: xAuthToken(user.id),
            userId: user.id
        )
        mongoTemplate.insert(fp);
        return fp;
    }

	/*
	 *  When
	 */
    
    protected HttpResponse whenPost(String postUrl, User user, String requestBody) {
        return whenPostBase(baseApiUrl + postUrl, user, requestBody);
    }
        
    protected HttpResponse whenPostAuth(String postUrl, User user, String requestBody) {
        return whenPostBase(baseAuthUrl + postUrl, user, requestBody);
    }
        
	private HttpResponse whenPostBase(String postUrl, User user, String requestBody) {
        postRequest = new HttpPost(postUrl)
        postRequest.addHeader("Content-Type", "application/json; charset=UTF-8")
        postRequest.addHeader("Accept", "application/json; charset=UTF-8")
        if (requestBody != null) {
            postRequest.setEntity(new StringEntity(requestBody, ContentType.APPLICATION_JSON))
        }
		if (user != null) {
            postRequest.addHeader("X-AUTH-TOKEN", xAuthToken(user.id))
		}
		HttpResponse resp = httpClient.execute(postRequest)
		return resp
	}

    protected HttpResponse whenPostUpload(String postUrl, User user, UploadRequest upload) {
        HttpEntity entity = MultipartEntityBuilder
            .create()
            .addTextBody("fileName", upload.getFileName())
            .addBinaryBody("file", new File("src/test/resources/" + upload.getFileName()), ContentType.create(upload.getMimeType()), upload.getFileName())
            .build();
    
        postRequest = new HttpPost(baseApiUrl + postUrl);
        postRequest.setEntity(entity);
        if (user != null) {
            postRequest.addHeader("X-AUTH-TOKEN", xAuthToken(user.id))
        }

        HttpResponse resp = httpClient.execute(postRequest);
        return resp
    }


	protected HttpResponse whenGet(String getUrl, User user = null, boolean relativeUrl = true) {
        getRequest = new HttpGet(relativeUrl ? (baseApiUrl + getUrl) : getUrl)
		getRequest.addHeader("Accept", "application/json; charset=UTF-8")
		if (user != null) {
            getRequest.addHeader("X-AUTH-TOKEN", xAuthToken(user.id))
		}
		HttpResponse resp = httpClient.execute(getRequest)
		return resp
	}

    protected HttpResponse whenGetFile(String getUrl, User user = null) {
        getRequest = new HttpGet(baseApiUrl + getUrl)
        if (user != null) {
            getRequest.addHeader("X-AUTH-TOKEN", xAuthToken(user.id))
        }
        HttpResponse resp = httpClient.execute(getRequest)
        return resp
    }

    protected HttpResponse whenPut(String putUrl, User user, String requestBody) {
        return whenPutBase(baseApiUrl + putUrl, user, requestBody);
    }
        
    protected HttpResponse whenPutAuth(String putUrl, User user, String requestBody) {
        return whenPutBase(baseAuthUrl + putUrl, user, requestBody);
    }
        
	private HttpResponse whenPutBase(String putUrl, User user, String requestBody) {
        putRequest = new HttpPut(putUrl)
        putRequest.addHeader("Content-Type", "application/json; charset=UTF-8")
        putRequest.addHeader("Accept", "application/json; charset=UTF-8")
        if (requestBody != null) {
            putRequest.setEntity(new StringEntity(requestBody, ContentType.APPLICATION_JSON))
        }
		if (user != null) {
            putRequest.addHeader("X-AUTH-TOKEN", xAuthToken(user.id))
		}
		HttpResponse resp = httpClient.execute(putRequest)
		return resp
	}

	protected HttpResponse whenDelete(String deleteUrl, User user) {
        deleteRequest = new HttpDelete(baseApiUrl + deleteUrl)
		deleteRequest.addHeader("Accept", "application/json; charset=UTF-8")
		if (user != null) {
            deleteRequest.addHeader("X-AUTH-TOKEN", xAuthToken(user.id))
		}
		HttpResponse resp = httpClient.execute(deleteRequest)
		return resp
	}

    protected HttpResponse whenLogin(String email, String password) {
        Map data = [username: email, password: password]
        
        postRequest = new HttpPost(baseUrl + "/auth/login")
        postRequest.addHeader("Content-Type", "application/json; charset=UTF-8")
        postRequest.addHeader("Accept", "application/json; charset=UTF-8")
        postRequest.setEntity(new StringEntity(toJSON(data), ContentType.APPLICATION_JSON))
        return httpClient.execute(postRequest)
    }

    protected String encodePassword(String password) {
        return new String(Base64.getUrlEncoder().encodeToString(password.getBytes("UTF-8")))
    }

    private String xAuthToken(String userId) {
        final String jwtSecret = "developmentSimpleJwtSecretToken"
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
    
    protected def readDb(Class entityClass, String itemId) {
        Query findOneQuery = Query.query(Criteria.where("id").is(QueryId.get(itemId)))
        return mongoTemplate.findOne(findOneQuery, entityClass)
    }
}
