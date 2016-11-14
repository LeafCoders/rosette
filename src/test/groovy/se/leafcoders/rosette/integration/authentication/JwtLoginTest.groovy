package se.leafcoders.rosette.integration.authentication

import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.web.client.RestTemplate
import se.leafcoders.rosette.RosetteApplication

@RunWith(SpringJUnit4ClassRunner.class)
//@WebAppConfiguration
@SpringBootTest(classes = RosetteApplication.class)
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