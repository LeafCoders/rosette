package se.leafcoders.rosette.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static se.leafcoders.rosette.matcher.Matchers.*;

import java.time.format.DateTimeFormatter;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import se.leafcoders.rosette.TimeRange;
import se.leafcoders.rosette.controller.dto.SlideIn;
import se.leafcoders.rosette.data.AssetFolderData;
import se.leafcoders.rosette.data.SlideData;
import se.leafcoders.rosette.data.SlideShowData;
import se.leafcoders.rosette.exception.ApiString;
import se.leafcoders.rosette.persistence.model.Asset;
import se.leafcoders.rosette.persistence.model.AssetFolder;
import se.leafcoders.rosette.persistence.model.Slide;
import se.leafcoders.rosette.persistence.model.SlideShow;
import se.leafcoders.rosette.persistence.repository.SlideShowRepository;

public class SlideShowsControllerSlidesTest extends AbstractControllerTest {

    @Autowired
    private SlideShowRepository slideShowRepository;

    private CommonRequestTests crt = new CommonRequestTests(this, Slide.class);

    private SlideShow extern;
    private Asset uploadImage1;

    @Before
    public void setup() throws Exception {
        super.setup();

        user1 = givenUser(user1);
        final AssetFolder assetFolder = givenAssetFolder(AssetFolderData.newAssetFolder());
        uploadImage1 = givenAssetInFolder(assetFolder.getId(), "image.png", "image.png", "image/png");

        extern = slideShowRepository.save(SlideShowData.extern(assetFolder));
    }

    private String url(SlideShow slideShow) {
        return "/slideShows/" + slideShow.getId() + "/slides";
    }

    private String permission(SlideShow slideShow, String action) {
        return "slideShows:" + action + ":" + slideShow.getId() + ":slides";
    }

    @Test
    public void getSlidesInSlideShow() throws Exception {
        Slide slide = SlideData.imageSlide(uploadImage1);
        extern.addSlide(slide);
        extern = slideShowRepository.save(extern);
        slide = extern.getSlides().iterator().next();

        crt.allGetManyTests(user1, permission(extern, "read"), url(extern))
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].id", isIdOf(slide)))
            .andExpect(jsonPath("$[0].title", is(slide.getTitle())))
            .andExpect(jsonPath("$[0].image.id", isIdOf(slide.getImage())));
    }

    @Test
    public void addSlideToSlideShow() throws Exception {
        SlideIn slide = SlideData.newSlide(uploadImage1, TimeRange.start(1, 12, 30).weekOffset(-1).endAfterDays(20));

        crt.allPostTests(user1, permission(extern, "create") + ",assets:read", url(extern), json(slide))
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
        String jsonData = mapToJson(
            data -> {
                data.put("title", "The only slide");
                data.put("duration", 34);
                data.put("startTime", newStartTime);
                data.put("endTime", newEndTime);
                return data;
            }
        );

        crt.allPutTests(user1, permission(extern, "update"), url(extern), slide.getId(), jsonData)
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

        crt.allDeleteTests(user1, permission(extern, "delete"), url(extern), slide.getId());
    }
}
