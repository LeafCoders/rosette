package se.leafcoders.rosette.endpoint.slideshow;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static se.leafcoders.rosette.test.matcher.Matchers.isIdOf;
import static se.leafcoders.rosette.test.matcher.Matchers.isValidationError;

import java.time.format.DateTimeFormatter;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import se.leafcoders.rosette.core.exception.ApiString;
import se.leafcoders.rosette.endpoint.AbstractControllerTest;
import se.leafcoders.rosette.endpoint.CommonRequestTests;
import se.leafcoders.rosette.endpoint.asset.Asset;
import se.leafcoders.rosette.endpoint.assetfolder.AssetFolder;
import se.leafcoders.rosette.endpoint.assetfolder.AssetFolderData;
import se.leafcoders.rosette.test.TimeRange;

public class SlideShowControllerSlideTest extends AbstractControllerTest {

    @Autowired
    private SlideShowRepository slideShowRepository;

    private final CommonRequestTests crt = new CommonRequestTests(this, Slide.class);

    private SlideShow extern;
    private Asset uploadImage1;

    @Before
    public void setup() throws Exception {
        super.setup();

        user1 = givenUser(user1);
        final AssetFolder assetFolder = givenAssetFolder(AssetFolderData.image());
        uploadImage1 = givenAssetInFolder(assetFolder.getId(), "image.png", "image.png", "image/png");

        extern = slideShowRepository.save(SlideShowData.extern(assetFolder));
    }

    private String url(SlideShow slideShow) {
        return "/api/slideShows/" + slideShow.getId() + "/slides";
    }

    @Test
    public void getSlidesInSlideShow() throws Exception {
        Slide slide = SlideData.imageSlide(uploadImage1);
        extern.addSlide(slide);
        extern = slideShowRepository.save(extern);
        slide = extern.getSlides().iterator().next();

        crt.allGetManyTests(user1, "slideShows:readSlides:" + extern.getId(), url(extern))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", isIdOf(slide)))
                .andExpect(jsonPath("$[0].title", is(slide.getTitle())))
                .andExpect(jsonPath("$[0].image.id", isIdOf(slide.getImage())));
    }

    @Test
    public void addSlideToSlideShow() throws Exception {
        final SlideIn slide = SlideData.newSlide(uploadImage1,
                TimeRange.start(1, 12, 30).weekOffset(-1).endAfterDays(20));

        crt.allPostTests(user1, "slideShows:createSlides:" + extern.getId() + ",assets:read", url(extern), json(slide))
                .andExpect(jsonPath("$.title", is(slide.getTitle())))
                .andExpect(jsonPath("$.image.id", is(slide.getImageId().intValue())));

        // Check missing properties
        crt.postExpectBadRequest(user1, url(extern), json(SlideData.missingAllProperties()))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]", isValidationError("imageId", ApiString.NOT_NULL)))
                .andExpect(jsonPath("$[1]", isValidationError("title", ApiString.STRING_NOT_EMPTY)));

        // Check invalid properties
        crt.postExpectBadRequest(user1, url(extern), json(SlideData.invalidProperties(uploadImage1)))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0]", isValidationError("duration", ApiString.DURATION_TOO_SHORT)))
                .andExpect(jsonPath("$[1]", isValidationError("endTime", ApiString.DATETIME_MUST_BE_AFTER)))
                .andExpect(jsonPath("$[2]", isValidationError("title", ApiString.STRING_NOT_EMPTY)));
    }

    @Test
    public void updateSlideInSlideShow() throws Exception {
        Slide slide = SlideData.imageSlide(uploadImage1);
        extern.addSlide(slide);
        extern = slideShowRepository.save(extern);
        slide = extern.getSlides().iterator().next();

        String newStartTime = slide.getEndTime().minusMinutes(2).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String newEndTime = slide.getEndTime().minusMinutes(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String jsonData = mapToJson(data -> {
            data.put("title", "The only slide");
            data.put("duration", 34);
            data.put("startTime", newStartTime);
            data.put("endTime", newEndTime);
        });

        crt.allPutTests(user1, "slideShows:updateSlides:" + extern.getId(), url(extern), slide.getId(), jsonData)
                .andExpect(jsonPath("$.id", isIdOf(slide)))
                .andExpect(jsonPath("$.title", is("The only slide")))
                .andExpect(jsonPath("$.duration", is(34)))
                .andExpect(jsonPath("$.startTime", is(newStartTime)))
                .andExpect(jsonPath("$.endTime", is(newEndTime)));
    }

    @Test
    public void deleteSlideInSlideShow() throws Exception {
        Slide slide = SlideData.imageSlide(uploadImage1);
        extern.addSlide(slide);
        extern = slideShowRepository.save(extern);
        slide = extern.getSlides().iterator().next();

        crt.allDeleteTests(user1, "slideShows:deleteSlides:" + extern.getId(), url(extern), slide.getId());
    }
}
