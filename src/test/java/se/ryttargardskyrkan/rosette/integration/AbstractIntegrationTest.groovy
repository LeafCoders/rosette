package se.ryttargardskyrkan.rosette.integration

import org.apache.http.impl.client.DefaultHttpClient
import org.codehaus.jackson.map.ObjectMapper
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.springframework.data.mongodb.core.MongoTemplate

import com.mongodb.Mongo
import com.mongodb.MongoException

abstract class AbstractIntegrationTest {
	protected static MongoTemplate mongoTemplate
	protected static DefaultHttpClient httpClient
	protected static ObjectMapper mapper
	
	protected String baseUrl = "http://localhost:9000/api/v1-snapshot"
	
	@BeforeClass
	static void beforeClass() throws UnknownHostException, MongoException {
		mongoTemplate = new MongoTemplate(new Mongo(), "rosette-test")
		httpClient = new DefaultHttpClient()
		mapper = new ObjectMapper()		
	}
	
	@Before
	public void before() {
		mongoTemplate.dropCollection("users")
		mongoTemplate.dropCollection("groups")
		mongoTemplate.dropCollection("events")
		mongoTemplate.dropCollection("themes")
	}
	
	@AfterClass
	static void afterClass() {
		mongoTemplate = null
		
		httpClient.getConnectionManager().shutdown()
		httpClient = null
		
		mapper = null
	}
}
