package se.leafcoders.rosette.endpoint.slideshow;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static se.leafcoders.rosette.test.matcher.Matchers.isIdOf;
import static se.leafcoders.rosette.test.matcher.Matchers.isValidationError;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import se.leafcoders.rosette.core.exception.ApiString;
import se.leafcoders.rosette.endpoint.AbstractControllerTest;
import se.leafcoders.rosette.endpoint.CommonRequestTests;
import se.leafcoders.rosette.endpoint.asset.Asset;
import se.leafcoders.rosette.endpoint.assetfolder.AssetFolder;
import se.leafcoders.rosette.endpoint.assetfolder.AssetFolderData;

public class SlideShowControllerTest extends AbstractControllerTest {

    @Autowired
    private SlideShowRepository slideShowRepository;

    private final CommonRequestTests crt = new CommonRequestTests(this, SlideShow.class);

    private AssetFolder assetFolder;

    @Before
    public void setup() throws Exception {
        super.setup();

        assetFolder = givenAssetFolder(AssetFolderData.image());
    }

    @Test
    public void getSlideShow() throws Exception {
        user1 = givenUser(user1);
        SlideShow extern = slideShowRepository.save(SlideShowData.extern(assetFolder));

        final Asset upload1 = givenAssetInFolder(assetFolder.getId(), "image.png", "image1.png", "image/png");
        final Asset upload2 = givenAssetInFolder(assetFolder.getId(), "image.png", "image2.png", "image/png");

        extern.addSlide(SlideData.imageSlide(upload1));
        extern.addSlide(SlideData.imageSlide(upload2));
        extern = slideShowRepository.save(extern);
        final Slide slide1 = (Slide) extern.getSlides().toArray()[0];
        final Slide slide2 = (Slide) extern.getSlides().toArray()[1];

        crt.allGetOneTests(user1, "slideShows:read", "/api/slideShows", extern.getId())
                .andExpect(jsonPath("$.idAlias", is(extern.getIdAlias())))
                .andExpect(jsonPath("$.name", is(extern.getName())))
                .andExpect(jsonPath("$.slides[*].id",
                        containsInAnyOrder(slide1.getId().intValue(), slide2.getId().intValue())));
    }

    @Test
    public void getSlideShows() throws Exception {
        user1 = givenUser(user1);
        final SlideShow extern = slideShowRepository.save(SlideShowData.extern(assetFolder));
        final SlideShow intern = slideShowRepository.save(SlideShowData.intern(assetFolder));

        crt.allGetManyTests(user1, "slideShows:read", "/api/slideShows")
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", isIdOf(extern)))
                .andExpect(jsonPath("$[1].id", isIdOf(intern)));
    }

    @Test
    public void createSlideShow() throws Exception {
        user1 = givenUser(user1);
        givenPermissionForUser(user1, "assetFolders:read:" + assetFolder.getId());
        final SlideShowIn extern = SlideShowData.newSlideShow(assetFolder);

        crt.allPostTests(user1, "slideShows:create", "/api/slideShows", json(extern))
                .andExpect(jsonPath("$.idAlias", is(extern.getIdAlias())))
                .andExpect(jsonPath("$.name", is(extern.getName())));

        // Check missing properties
        crt.postExpectBadRequest(user1, "/api/slideShows", json(SlideShowData.missingAllProperties()))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0]", isValidationError("assetFolderId", ApiString.NOT_NULL)))
                .andExpect(jsonPath("$[1]", isValidationError("idAlias", ApiString.STRING_NOT_EMPTY)))
                .andExpect(jsonPath("$[2]", isValidationError("name", ApiString.STRING_NOT_EMPTY)));

        // Check invalid properties
        crt.postExpectBadRequest(user1, "/api/slideShows", json(SlideShowData.invalidProperties()))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]", isValidationError("idAlias", ApiString.IDALIAS_INVALID_FORMAT)))
                .andExpect(jsonPath("$[1]", isValidationError("name", ApiString.STRING_NOT_EMPTY)));
    }

    @Test
    public void updateSlideShow() throws Exception {
        user1 = givenUser(user1);
        final SlideShow extern = slideShowRepository.save(SlideShowData.extern(assetFolder));

        String jsonData = mapToJson(data -> data.put("name", "Public slide show"));

        crt.allPutTests(user1, "slideShows:update", "/api/slideShows", extern.getId(), jsonData)
                .andExpect(jsonPath("$.idAlias", is(extern.getIdAlias())))
                .andExpect(jsonPath("$.name", is("Public slide show")));
    }

    @Test
    public void deleteSlideShow() throws Exception {
        user1 = givenUser(user1);
        final SlideShow intern = slideShowRepository.save(SlideShowData.intern(assetFolder));

        crt.allDeleteTests(user1, "slideShows:delete", "/api/slideShows", intern.getId());
    }
}
