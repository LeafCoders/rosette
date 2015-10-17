package se.leafcoders.rosette.integration.authentication

import org.junit.Ignore
import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.boot.test.TestRestTemplate
import org.springframework.boot.test.WebIntegrationTest
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.web.client.RestTemplate
import se.leafcoders.rosette.RosetteApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = RosetteApplication.class)
//@WebAppConfiguration
@WebIntegrationTest
@Ignore
public class JwtLoginTest {
 
    private RestTemplate restTemplate = new TestRestTemplate();
    
    @Before
    public void setUp() {
    }
 
    @Test
    public void canFetchMickey() {
    }
 
}