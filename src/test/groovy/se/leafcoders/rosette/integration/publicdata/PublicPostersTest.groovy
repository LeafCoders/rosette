package se.leafcoders.rosette.integration.publicdata

import java.io.IOException;
import java.text.SimpleDateFormat;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.joda.time.DateTime;
import org.junit.Test;
import com.mongodb.util.JSON;
import se.leafcoders.rosette.integration.AbstractIntegrationTest;
import se.leafcoders.rosette.integration.util.TestUtil;
import se.leafcoders.rosette.model.upload.UploadFile

public class PublicPostersTest extends AbstractIntegrationTest {

	@Test
	public void readAllActivePostersFoundTwo() throws ClientProtocolException, IOException {
		// Given
		poster1.startTime = new DateTime(new Date()).minusMinutes(5).toDate();
		poster1.endTime = new DateTime(new Date()).plusMinutes(10).toDate();
		poster2.startTime = new DateTime(new Date()).minusDays(10).toDate();
		poster2.endTime = new DateTime(new Date()).plusDays(5).toDate();
		givenPermissionForEveryone(["public:read"])
		givenUploadFolder(uploadFolderPosters)
		UploadFile uploadItem = givenUploadInFolder("posters", validPNGImage)
		givenPoster(poster1, uploadItem)
		givenPoster(poster2, uploadItem)

		// When
		String getUrl = "/public/posters"
		HttpResponse getResponse = whenGet(getUrl)

		// Then
		String responseBody = thenResponseCodeIs(getResponse, HttpServletResponse.SC_OK)
		thenResponseHeaderHas(getResponse, "Content-Type", "application/json;charset=UTF-8")

		String expectedData = """[
			{
				"id" : "${ poster2.id }",
				"title" : "Poster2 title",
				"startTime" : "${ TestUtil.dateToModelTime(poster2.startTime) }",
				"endTime" : "${ TestUtil.dateToModelTime(poster2.endTime) }",
				"duration" : 10,
				"image" : ${ toJSON(uploadItem) }
			},
			{
				"id" : "${ poster1.id }",
				"title" : "Poster1 title",
				"startTime" : "${ TestUtil.dateToModelTime(poster1.startTime) }",
				"endTime" : "${ TestUtil.dateToModelTime(poster1.endTime) }",
				"duration" : 15,
				"image" : ${ toJSON(uploadItem) }
			}
		]"""
		thenResponseDataIs(responseBody, expectedData)
	}

	@Test
	public void readAllActivePostersNoneFound() throws ClientProtocolException, IOException {
		// Given
		poster1.startTime = new DateTime(new Date()).plusMinutes(5).toDate();
		poster1.endTime = new DateTime(new Date()).plusMinutes(10).toDate();
		poster2.startTime = new DateTime(new Date()).minusMinutes(15).toDate();
		poster2.endTime = new DateTime(new Date()).minusMinutes(5).toDate();
		givenPermissionForEveryone(["public:read"])
		givenUploadFolder(uploadFolderPosters)
		UploadFile uploadItem = givenUploadInFolder("posters", validPNGImage)
		givenPoster(poster1, uploadItem)
		givenPoster(poster2, uploadItem)

		// When
		String getUrl = "/public/posters"
		HttpResponse getResponse = whenGet(getUrl)

		// Then
		String responseBody = thenResponseCodeIs(getResponse, HttpServletResponse.SC_OK)
		thenResponseHeaderHas(getResponse, "Content-Type", "application/json;charset=UTF-8")
		thenResponseDataIs(responseBody, "[]")
	}
}
