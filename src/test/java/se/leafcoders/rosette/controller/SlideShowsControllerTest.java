package se.leafcoders.rosette.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static se.leafcoders.rosette.matcher.Matchers.isIdOf;
import static se.leafcoders.rosette.matcher.Matchers.isValidationError;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import se.leafcoders.rosette.controller.dto.SlideShowIn;
import se.leafcoders.rosette.data.AssetFolderData;
import se.leafcoders.rosette.data.SlideData;
import se.leafcoders.rosette.data.SlideShowData;
import se.leafcoders.rosette.exception.ApiString;
import se.leafcoders.rosette.persistence.model.Asset;
import se.leafcoders.rosette.persistence.model.AssetFolder;
import se.leafcoders.rosette.persistence.model.Slide;
import se.leafcoders.rosette.persistence.model.SlideShow;
import se.leafcoders.rosette.persistence.repository.SlideShowRepository;

public class SlideShowsControllerTest extends AbstractControllerTest {

    @Autowired
    private SlideShowRepository slideShowRepository;

    private CommonRequestTests crt = new CommonRequestTests(this, SlideShow.class);

    private AssetFolder assetFolder;
    
    @Before
    public void setup() throws Exception {
        super.setup();
        
        assetFolder = givenAssetFolder(AssetFolderData.newAssetFolder());
    }

    @Test
    public void getSlideShow() throws Exception {
        user1 = givenUser(user1);
        SlideShow extern = slideShowRepository.save(SlideShowData.extern(assetFolder));

        Asset upload1 = givenAssetInFolder(assetFolder.getId(), "image.png", "image1.png", "image/png");
        Asset upload2 = givenAssetInFolder(assetFolder.getId(), "image.png", "image2.png", "image/png");
        
        extern.addSlide(SlideData.imageSlide(upload1));
        extern.addSlide(SlideData.imageSlide(upload2));
        extern = slideShowRepository.save(extern);
        Slide slide1 = (Slide) extern.getSlides().toArray()[0];
        Slide slide2 = (Slide) extern.getSlides().toArray()[1];

        crt.allGetOneTests(user1, "slideShows:read", "/slideShows", extern.getId())
            .andExpect(jsonPath("$.idAlias", is(extern.getIdAlias())))
            .andExpect(jsonPath("$.name", is(extern.getName())))
            .andExpect(jsonPath("$.slides[*].id", containsInAnyOrder(slide1.getId().intValue(), slide2.getId().intValue())));
    }

    @Test
    public void getSlideShows() throws Exception {
        user1 = givenUser(user1);
        SlideShow extern = slideShowRepository.save(SlideShowData.extern(assetFolder));
        SlideShow intern = slideShowRepository.save(SlideShowData.intern(assetFolder));

        crt.allGetManyTests(user1, "slideShows:read", "/slideShows")
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id", isIdOf(extern)))
            .andExpect(jsonPath("$[1].id", isIdOf(intern)));
    }

    @Test
    public void createSlideShow() throws Exception {
        user1 = givenUser(user1);
        SlideShowIn extern = SlideShowData.newSlideShow(assetFolder);

        crt.allPostTests(user1, "slideShows:create", "/slideShows", json(extern))
            .andExpect(jsonPath("$.idAlias", is(extern.getIdAlias())))
            .andExpect(jsonPath("$.name", is(extern.getName())));

        // Check missing properties
        crt.postExpectBadRequest(user1, "/slideShows", json(SlideShowData.missingAllProperties()))
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0]", isValidationError("idAlias", ApiString.STRING_NOT_EMPTY)))
            .andExpect(jsonPath("$[1]", isValidationError("name", ApiString.STRING_NOT_EMPTY)));

        // Check invalid properties
        crt.postExpectBadRequest(user1, "/slideShows", json(SlideShowData.invalidProperties()))
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0]", isValidationError("idAlias", ApiString.IDALIAS_INVALID_FORMAT)))
            .andExpect(jsonPath("$[1]", isValidationError("name", ApiString.STRING_NOT_EMPTY)));
    }

    @Test
    public void updateSlideShow() throws Exception {
        user1 = givenUser(user1);
        SlideShow extern = slideShowRepository.save(SlideShowData.extern(assetFolder));

        String jsonData = mapToJson(
            data -> {
                data.put("name", "Public slide show");
                return data;
            }
        );

        crt.allPutTests(user1, "slideShows:update", "/slideShows", extern.getId(), jsonData)
            .andExpect(jsonPath("$.idAlias", is(extern.getIdAlias())))
            .andExpect(jsonPath("$.name", is("Public slide show")));
    }

    @Test
    public void deleteSlideShow() throws Exception {
        user1 = givenUser(user1);
        SlideShow intern = slideShowRepository.save(SlideShowData.intern(assetFolder));

        crt.allDeleteTests(user1, "slideShows:delete", "/slideShows", intern.getId());
    }
}
