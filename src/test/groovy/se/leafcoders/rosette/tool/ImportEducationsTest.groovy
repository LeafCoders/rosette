package se.leafcoders.rosette.tool

import static org.junit.Assert.*
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.NameValuePair
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.HttpClient
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.entity.mime.MultipartEntityBuilder
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.message.BasicNameValuePair
import org.junit.Test
import org.springframework.http.HttpStatus
import se.leafcoders.rosette.integration.util.TestUtil
import se.leafcoders.rosette.model.education.Education
import se.leafcoders.rosette.model.education.EducationThemeRef
import se.leafcoders.rosette.model.education.EducationTypeRef
import se.leafcoders.rosette.model.education.SimpleEducation
import se.leafcoders.rosette.model.reference.UserRef
import se.leafcoders.rosette.model.reference.UserRefOrText
import se.leafcoders.rosette.model.upload.UploadResponse
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.util.JSON

public class ImportEducationsTest {

    private HttpClient httpClient
    private ObjectMapper mapper
    
    private String authJwt
    
    private String rosetteBaseUrl
    private String username
    private String password
    private String educationsCSV
    private String recordingsPath
    private String uploadFolder
    
    private String getValue(String key) {
        String value = System.getProperty(key)
        assertNotNull("Please specify -D${ key }=...", value)
        return value
    }
    
    private void readInputs() {
        rosetteBaseUrl = getValue("rosette.baseUrl")
        username = getValue("rosette.username")
        password = getValue("rosette.password")
        educationsCSV = getValue("rosette.educationsCSV")
        recordingsPath = getValue("rosette.recordingsPath")
        uploadFolder = getValue("rosette.uploadFolder")
    }

    @Test
    public void importEducations() {
        readInputs()

        httpClient = HttpClientBuilder.create().build()
        mapper = new ObjectMapper()

        authJwt = requestAuthJwt()

        readEducationsFromFile().each { Education education ->
            postEducation(education)
        }
    }

    private String requestAuthJwt() throws URISyntaxException, ClientProtocolException, IOException {
        List<NameValuePair> parameters = new ArrayList<NameValuePair>(2);
        parameters.add(new BasicNameValuePair("username", username));
        parameters.add(new BasicNameValuePair("password", Base64.encodeBase64URLSafeString(password.getBytes())));

        HttpPost request = new HttpPost(rosetteBaseUrl + "/auth/login")
        request.setEntity(new UrlEncodedFormEntity(parameters))
        HttpResponse response = httpClient.execute(request)
        request.releaseConnection()

        if (response.getStatusLine().getStatusCode() == HttpStatus.UNAUTHORIZED.value()) {
            fail("Failed to authorize. Username or password is invalid.")
        } else if (response.getStatusLine().getStatusCode() != HttpStatus.OK.value()) {
            fail("Failed to login. Responce code: " + response.getStatusLine().getStatusCode() + " from " + rosetteBaseUrl + "/auth/login")
        }
        return response.getFirstHeader("X-AUTH-TOKEN").getValue()
    }

    private List<Education> readEducationsFromFile() throws URISyntaxException, ClientProtocolException, IOException {
        List<Education> educations = []
        new File(educationsCSV).eachLine { String line ->
            if (line.trim().length()) {
                List<String> values = line.split(",")

                assertEquals("Invalid line in file ${ educationsCSV }: ${ line }", 7, values.size())

                String educationTypeId = values[0]
                String educationThemeId = values[1]
                String time = values[2]
                String authorName = values[3]
                String authorUserId = values[4]
                String title = values[5]
                String recording = values[6]

                assertTrue("Missing educationTypeId in line: ${ line }", !educationTypeId.isEmpty())
                assertTrue("Missing educationThemeId in line: ${ line }", !educationThemeId.isEmpty())
                assertTrue("Missing time in line: ${ line }", !time.isEmpty())
                assertTrue("One of authorName and authorUserId must be specified in line: ${ line }", !!authorName != !!authorUserId )
                assertTrue("Missing title in line: ${ line }", !title.isEmpty())
                assertTrue("Missing recordingFileName in line: ${ line }", !recording.isEmpty())
                assertTrue("Recording file ${ recordingsPath + '/' + recording } was not found", new File(recordingsPath + "/" + recording).exists())

                educations.push(new SimpleEducation(
                    type : 'simple',
                    educationType : new EducationTypeRef(id: educationTypeId),
                    educationTheme : new EducationThemeRef(id: educationThemeId),
                    time: TestUtil.modelDate("${ time } Europe/Stockholm"),
                    title : title,
                    recording : postRecording(recording),
                    author : new UserRefOrText(ref: (authorUserId ? new UserRef(id: authorUserId) : null), text: authorName)
                ))
            }
        }
        return educations
    }

    private void postEducation(Education education) throws URISyntaxException, ClientProtocolException, IOException {
        HttpPost request = new HttpPost(rosetteBaseUrl + "/api/v1/educations")
        request.setEntity(new StringEntity(mapper.writeValueAsString(education), "application/json", "UTF-8"))
        request.addHeader("X-AUTH-TOKEN", authJwt)
        httpClient.execute(request)
        request.releaseConnection()
    }

    private UploadResponse postRecording(String fileName) throws IllegalStateException, IOException {
        HttpEntity entity = MultipartEntityBuilder
            .create()
            .addTextBody("fileName", fileName)
            .addBinaryBody("file", new File(recordingsPath + "/" + fileName), ContentType.create("audio/mp3"), fileName)
            .build()
    
        HttpPost request = new HttpPost(rosetteBaseUrl + "/api/v1/uploads/${ uploadFolder }")
        request.setEntity(entity)
        request.addHeader("X-AUTH-TOKEN", authJwt)
        HttpResponse response = httpClient.execute(request)

        UploadResponse uploadResponse = (UploadResponse) JSON.parse(TestUtil.jsonFromResponse(response))
        request.releaseConnection()
        return uploadResponse
    }
    
}
