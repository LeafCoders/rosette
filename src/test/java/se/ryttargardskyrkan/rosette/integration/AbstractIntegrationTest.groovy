package se.ryttargardskyrkan.rosette.integration

import org.apache.http.HttpResponse
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.methods.HttpDelete
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpPut
import org.apache.http.entity.StringEntity
import org.apache.http.impl.auth.BasicScheme
import org.apache.http.impl.client.DefaultHttpClient
import org.bson.types.ObjectId
import org.codehaus.jackson.map.ObjectMapper
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.gridfs.GridFsTemplate
import se.ryttargardskyrkan.rosette.integration.util.TestUtil
import se.ryttargardskyrkan.rosette.security.RosettePasswordService
import com.mongodb.Mongo
import com.mongodb.MongoException
import com.mongodb.util.JSON
import javax.servlet.http.HttpServletResponse
import static junit.framework.Assert.assertEquals
import static junit.framework.Assert.assertTrue

abstract class AbstractIntegrationTest {
	protected static MongoTemplate mongoTemplate
	protected static GridFsTemplate gridFsTemplate
	protected static DefaultHttpClient httpClient
	protected static ObjectMapper mapper

	protected static String baseUrl = "http://localhost:9000/api/v1-snapshot"

	@BeforeClass
	static void beforeClass() throws UnknownHostException, MongoException {
		// Clearing auth cache
		httpClient = new DefaultHttpClient();
		HttpDelete httpDelete = new HttpDelete(baseUrl + "/authCaches")
		httpClient.execute(httpDelete)
		httpClient.getConnectionManager().shutdown()
		httpClient = null
		
		mongoTemplate = new MongoTemplate(new Mongo(), "rosette-test")
		gridFsTemplate = new GridFsTemplate(mongoTemplate.mongoDbFactory, mongoTemplate.converter)
		httpClient = new DefaultHttpClient()
		mapper = new ObjectMapper()		
	}
	
	@Before
	public void before() {
		mongoTemplate.dropCollection("users")
		mongoTemplate.dropCollection("groups")
		mongoTemplate.dropCollection("groupMemberships")
		mongoTemplate.dropCollection("events")
        mongoTemplate.dropCollection("eventTypes")
		mongoTemplate.dropCollection("themes")
		mongoTemplate.dropCollection("permissions")
        mongoTemplate.dropCollection("posters")
        mongoTemplate.dropCollection("userResourceTypes")
        mongoTemplate.dropCollection("locations")
        mongoTemplate.dropCollection("bookings")
        gridFsTemplate.delete(null)
	}
	
	@AfterClass
	static void afterClass() {
		gridFsTemplate = null
		mongoTemplate = null
		
		httpClient.getConnectionManager().shutdown()
		httpClient = null
		
		mapper = null		
	}

	/*
	 *  Helper data and methods
	 */
	private int objectId = 1
	private int getObjectId() { return objectId++ }
	private def toReference(String itemId, def item) {
		def reference = JSON.parse("""{"idRef":"${itemId}", "text":null, "referredObject":null}""")
		reference['referredObject'] = item
		return reference
	}

	private boolean hasAddedUploadUser = false
	private void createTestUploadUser() {
		if (!hasAddedUploadUser) {
			hasAddedUploadUser = true
			givenUser(userTestUpload)
			givenPermissionForUser(userTestUpload, """["*:uploads"]""")
		}
	}

	/*
	 *  Data objects
	 */
	private final hashedPassword = new RosettePasswordService().encryptPassword("password")
	private final userTestUpload = JSON.parse("""
		{
			"_id" : "${getObjectId()}",
			"username" : "usertestupload",
			"hashedPassword" : "${hashedPassword}",
			"status" : "active"
		}
		""")
	protected final user1 = JSON.parse("""
		{
			"_id" : "${getObjectId()}",
			"username" : "user1",
			"hashedPassword" : "${hashedPassword}",
			"status" : "active"
		}
		""")
	protected final poster1 = JSON.parse("""
		{
			"_id" : "${getObjectId()}",
			"title" : "Poster1 title",
			"startTime" : ${TestUtil.mongoDate("2012-03-25 11:00 Europe/Stockholm")},
			"endTime" : ${TestUtil.mongoDate("2012-03-26 11:00 Europe/Stockholm")},
			"duration" : 15,
            "image" : null
		}
		""")
	protected final poster2 = JSON.parse("""
		{
			"_id" : "${getObjectId()}",
			"title" : "Poster2 title",
			"startTime" : ${TestUtil.mongoDate("2012-03-26 10:00 Europe/Stockholm")},
			"endTime" : ${TestUtil.mongoDate("2012-03-27 18:00 Europe/Stockholm")},
			"duration" : 10,
            "image" : null
		}
		""")
	protected final String validPNGImage = """
        {
            "fileName" : "image.png",
            "mimeType" : "image/png",
            "fileData" : "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAABAWlDQ1BJQ0MgUHJvZmlsZQAAGBljYGC8k1hQkMPEwMCQm1dSFOTupBARGaUA5MJBYnJxgWNAgA9IIC8/LxUuAWd8u8bACOJc1nV2D1ao2TrN8VKr2JnJE+fttOuadAGuDDuDo7ykoAQo9QSIRYqAlgPpHyB2OpjNyANiJ0HYCiB2UUiQMwMDowmQzZcOYbuA2EkQdgiInZJanAxUkwJklyH88zkE7E5GsZMIsfwFDAyW8gwMzN0IsaRpDAzb9zMwSJxBiKkA1fHbMDBsO5dcWlQGNBcEGBnPMjAQ4kPcAlYv456al1qUmawQUJRZlliSqgAK74Ci/LTMHCzBCtZCPgEAA1NJWOMhui0AAAAJcEhZcwAACxMAAAsTAQCanBgAAAI9aVRYdFhNTDpjb20uYWRvYmUueG1wAAAAAAA8eDp4bXBtZXRhIHhtbG5zOng9ImFkb2JlOm5zOm1ldGEvIiB4OnhtcHRrPSJYTVAgQ29yZSA1LjQuMCI+CiAgIDxyZGY6UkRGIHhtbG5zOnJkZj0iaHR0cDovL3d3dy53My5vcmcvMTk5OS8wMi8yMi1yZGYtc3ludGF4LW5zIyI+CiAgICAgIDxyZGY6RGVzY3JpcHRpb24gcmRmOmFib3V0PSIiCiAgICAgICAgICAgIHhtbG5zOnRpZmY9Imh0dHA6Ly9ucy5hZG9iZS5jb20vdGlmZi8xLjAvIj4KICAgICAgICAgPHRpZmY6WFJlc29sdXRpb24+NzI8L3RpZmY6WFJlc29sdXRpb24+CiAgICAgICAgIDx0aWZmOlJlc29sdXRpb25Vbml0PjI8L3RpZmY6UmVzb2x1dGlvblVuaXQ+CiAgICAgICAgIDx0aWZmOllSZXNvbHV0aW9uPjcyPC90aWZmOllSZXNvbHV0aW9uPgogICAgICAgICA8dGlmZjpPcmllbnRhdGlvbj4xPC90aWZmOk9yaWVudGF0aW9uPgogICAgICAgICA8dGlmZjpQaG90b21ldHJpY0ludGVycHJldGF0aW9uPjI8L3RpZmY6UGhvdG9tZXRyaWNJbnRlcnByZXRhdGlvbj4KICAgICAgPC9yZGY6RGVzY3JpcHRpb24+CiAgIDwvcmRmOlJERj4KPC94OnhtcG1ldGE+CjAVYfMAAABzSURBVDgR1VFBCsAwCFvH3lrf5Gs3OgjEOEehh7FeotXEiK33fm4Lb1/g3tTvBY5qBXcPJTMLOZLXFQapIkKg6RV0MhqBKhgcKPnJgfYEgTEFE4D8BxeMSYCLM3EpwFY5VtF0Rm7mWInI0xVQmMVyhf8IXIo8H+rM3fTBAAAAAElFTkSuQmCC"
        }
        """
	protected final String validJPEGImage = """
        {
            "fileName" : "image.jpg",
            "mimeType" : "image/jpg",
            "fileData" : "data:image/jpg;base64,/9j/4AAQSkZJRgABAQEASABIAAD/4gHsSUNDX1BST0ZJTEUAAQEAAAHcYXBwbAIAAABtbnRyUkdCIFhZWiAAAAAAAAAAAAAAAABhY3NwQVBQTAAAAABub25lAAAAAAAAAAAAAAAAAAAAAAAA9tYAAQAAAADTLUNHUyB8tZZB0oUWzJORnrk+ipLQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAh3dHB0AAAA5AAAABRyWFlaAAAA+AAAABRnWFlaAAABDAAAABRiWFlaAAABIAAAABRyVFJDAAABNAAAAA5nVFJDAAABRAAAAA5iVFJDAAABVAAAAA5kZXNjAAABZAAAAHZYWVogAAAAAAAA81QAAQAAAAEWyVhZWiAAAAAAAABvoAAAOR8AAAOLWFlaIAAAAAAAAGKWAAC3vwAAGMxYWVogAAAAAAAAJKAAAA88AAC2zmN1cnYAAAAAAAAAAQHNAABjdXJ2AAAAAAAAAAEBzQAAY3VydgAAAAAAAAABAc0AAGRlc2MAAAAAAAAAHEdlbmVyaWMgUHJpdmF0ZSBSR0IgUHJvZmlsZQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAP/hAIxFeGlmAABNTQAqAAAACAAGAQYAAwAAAAEAAgAAARIAAwAAAAEAAQAAARoABQAAAAEAAABWARsABQAAAAEAAABeASgAAwAAAAEAAgAAh2kABAAAAAEAAABmAAAAAAAAAEgAAAABAAAASAAAAAEAAqACAAQAAAABAAAAEKADAAQAAAABAAAAEAAAAAD/2wBDAAIBAQIBAQICAQICAgICAwUDAwMDAwYEBAMFBwYHBwcGBgYHCAsJBwgKCAYGCQ0JCgsLDAwMBwkNDg0MDgsMDAv/2wBDAQICAgMCAwUDAwULCAYICwsLCwsLCwsLCwsLCwsLCwsLCwsLCwsLCwsLCwsLCwsLCwsLCwsLCwsLCwsLCwsLCwv/wAARCAAQABADASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwBf2Tf2IfFP7Usera1aw3Wk+BPDcNzLrPiJhbCGzaK1knEaC6ubeORmKRqx81ViEyPIyIQSftZfsQ+Kf2Wo9J1q6hutW8CeJIbaXRvESi2MN40trHOY3Frc3EcbKXkVT5rLKIXeNnQEi18C/wBqrwp8O/gNrPgT4p+A9T8V2ur/ANop9otPEY0vyEuzpUm4J9klJlim0W2kRi2w7mV43Fbn7Qn7dWg/Fb9jvwh8G/hn8Prrwnovg/U01KC8ufEJ1Oa5YR3Ik8wfZogGkku3lJUhQcqqKuAoB//Z"
        }
        """

	/*
	 *  Given
	 */
	protected void givenUser(def user) {
		mongoTemplate.getCollection("users").insert([user]);
	}

	protected void givenPermissionForUser(def user, String permission) {
		mongoTemplate.getCollection("permissions").insert(JSON.parse("""
			[{
				"_id" : "${getObjectId()}",
				"userId" : "${user['_id']}",
				"patterns" : ${permission}
			}]
			"""))
	}

	protected void givenPoster(def poster, def upload) {
		poster['image'] = toReference(upload['id'], upload)
		mongoTemplate.getCollection("posters").insert([poster]);
	}
	
	protected Object givenUploadInFolder(String folder, def upload) {
		createTestUploadUser()
        HttpPost postRequest = new HttpPost(baseUrl + "/uploads/" + folder)
		HttpResponse postResponse = whenPost(postRequest, userTestUpload, upload)
		thenResponseCodeIs(postResponse, HttpServletResponse.SC_CREATED)
		Object responseObj = JSON.parse(TestUtil.jsonFromResponse(postResponse))
		postRequest.releaseConnection()
		return responseObj; 
	}

	

	/*
	 *  When
	 */
	protected HttpResponse whenPost(HttpPost postRequest, def user, String requestBody) {
		postRequest.setEntity(new StringEntity(requestBody, "application/json", "UTF-8"))
		postRequest.addHeader(new BasicScheme().authenticate(new UsernamePasswordCredentials(user['username'], "password"), postRequest))
		HttpResponse resp = httpClient.execute(postRequest)
		return resp
	}

	protected HttpResponse whenGet(HttpGet getRequest, def user) {
		getRequest.addHeader("Accept", "application/json; charset=UTF-8")
		getRequest.addHeader("Content-Type", "application/json; charset=UTF-8")
		getRequest.addHeader(new BasicScheme().authenticate(new UsernamePasswordCredentials(user['username'], "password"), getRequest))
		HttpResponse resp = httpClient.execute(getRequest)
		return resp
	}

	protected HttpResponse whenPut(HttpPut putRequest, def user, String requestBody) {
		putRequest.setEntity(new StringEntity(requestBody, "application/json", "UTF-8"))
		putRequest.addHeader(new BasicScheme().authenticate(new UsernamePasswordCredentials(user['username'], "password"), putRequest))
		HttpResponse resp = httpClient.execute(putRequest)
		return resp
	}

	protected HttpResponse whenDelete(HttpDelete deleteRequest, def user) {
		deleteRequest.addHeader("Accept", "application/json; charset=UTF-8")
		deleteRequest.addHeader(new BasicScheme().authenticate(new UsernamePasswordCredentials(user['username'], "password"), deleteRequest))
		HttpResponse resp = httpClient.execute(deleteRequest)
		return resp
	}

	/*
	 *  Then
	 */
	protected void thenResponseCodeIs(HttpResponse response, int code) {
		assertEquals(code, response.getStatusLine().getStatusCode())
	}

	protected void thenResponseHeaderHas(HttpResponse response, String type, String value) {
		assertEquals(value, response.getHeaders(type)[0].getValue())
	}

	protected void thenResponseDataIs(String responseBody, String expectedBody) {
		TestUtil.assertJsonEquals(expectedBody, responseBody)
	}

	protected void thenDataInDatabaseIs(Class entityClass, String expectedBody) {
		List<Object> inDatabase = mongoTemplate.findAll(entityClass)
		TestUtil.assertJsonEquals(expectedBody, new ObjectMapper().writeValueAsString(inDatabase))
	}

	protected void thenItemsInDatabaseIs(Class entityClass, Long count) {
		assertEquals(count, mongoTemplate.count(new Query(), entityClass))
	}

	protected void thenAssetWithNameExist(String fileName, String fileUrl) {
		createTestUploadUser()
		HttpGet getAssetsRequest = new HttpGet(fileUrl)
		HttpResponse assetsResponse = whenGet(getAssetsRequest, userTestUpload)
		assertEquals(HttpServletResponse.SC_OK, assetsResponse.getStatusLine().getStatusCode())
		assertTrue(assetsResponse.allHeaders.toString().contains(fileName))
		getAssetsRequest.releaseConnection()
	}

	protected void thenAssetDontExist(String fileUrl) {
		createTestUploadUser()
		HttpGet getAssetsRequest = new HttpGet(fileUrl)
		HttpResponse assetsResponse = whenGet(getAssetsRequest, userTestUpload)
		assertEquals(HttpServletResponse.SC_NOT_FOUND, assetsResponse.getStatusLine().getStatusCode())
		getAssetsRequest.releaseConnection()
	}
}
