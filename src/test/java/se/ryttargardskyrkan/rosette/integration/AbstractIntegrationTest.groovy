package se.ryttargardskyrkan.rosette.integration

import java.net.UnknownHostException;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.Mongo
import com.mongodb.MongoException;

abstract class AbstractIntegrationTest {
	protected static MongoTemplate mongoTemplate
	protected static HttpClient httpClient
	protected static ObjectMapper mapper
	
	@BeforeClass
	static void beforeClass() throws UnknownHostException, MongoException {
		mongoTemplate = new MongoTemplate(new Mongo(), "rosette-test")
		httpClient = new DefaultHttpClient()
		mapper = new ObjectMapper()
	}
	
	@AfterClass
	static void afterClass() {
		mongoTemplate = null
		
		httpClient.getConnectionManager().shutdown()
		httpClient = null
		
		mapper = null
	}
}
