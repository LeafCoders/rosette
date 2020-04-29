package se.leafcoders.rosette.controller;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static se.leafcoders.rosette.matcher.Matchers.isIdOf;
import static se.leafcoders.rosette.matcher.Matchers.isValidationError;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import se.leafcoders.rosette.controller.dto.AssetIn;
import se.leafcoders.rosette.data.AssetData;
import se.leafcoders.rosette.exception.ApiString;
import se.leafcoders.rosette.persistence.model.Asset;
import se.leafcoders.rosette.persistence.repository.AssetRepository;

public class AssetsControllerTest extends AbstractControllerTest {

    @Autowired
    private AssetRepository assetRepository;

    private CommonRequestTests crt = new CommonRequestTests(this, Asset.class);

    @Before
    public void setup() throws Exception {
        super.setup();
    }

    @Test
    public void getAsset() throws Exception {
        user1 = givenUser(user1);
        Asset asset = assetRepository.save(AssetData.fileAsset());

        crt.allGetOneTests(user1, "assets:read", "/assets", asset.getId())
            .andExpect(jsonPath("$.type", is(asset.getType().name())))
            .andExpect(jsonPath("$.mimeType", is(asset.getMimeType())))
            .andExpect(jsonPath("$.fileName", is(asset.getFileName())))
            .andExpect(jsonPath("$.fileSize", is(asset.getFileSize().intValue())))
            .andExpect(jsonPath("$.width", is(asset.getWidth().intValue())))
            .andExpect(jsonPath("$.height", is(asset.getHeight().intValue())));
    }

    @Test
    public void getAssets() throws Exception {
        user1 = givenUser(user1);
        Asset asset1 = assetRepository.save(AssetData.fileAsset());
        Asset asset2 = assetRepository.save(AssetData.fileAsset());

        crt.allGetManyTests(user1, "assets:read", "/assets")
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id", isIdOf(asset1)))
            .andExpect(jsonPath("$[1].id", isIdOf(asset2)));
    }

    @Test
    public void createAsset() throws Exception {
        user1 = givenUser(user1);
        AssetIn asset = AssetData.newUrlAsset();

        crt.allPostTests(user1, "assets:create", "/assets", json(asset))
        .andExpect(jsonPath("$.type", is(asset.getType())))
        .andExpect(jsonPath("$", allOf(hasKey("id"), hasKey("mimeType"), hasKey("fileName"), hasKey("fileSize"))));

        // Check missing properties
        crt.postExpectBadRequest(user1, "/assets", json(AssetData.missingAllProperties()))
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0]", isValidationError("type", ApiString.STRING_NOT_EMPTY)));

        // Check invalid properties
        crt.postExpectBadRequest(user1, "/assets", json(AssetData.invalidProperties()))
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0]", isValidationError("type", ApiString.STRING_NOT_ANY_OF)));
    }

    @Test
    public void updateAsset() throws Exception {
        user1 = givenUser(user1);
        Asset asset = assetRepository.save(AssetData.fileAsset());

        String jsonData = mapToJson(data -> data.put("url", "http://new.url"));

        crt.allPutTests(user1, "assets:update", "/assets", asset.getId(), jsonData)
            .andExpect(jsonPath("$.url", is("http://new.url")));
    }

    @Test
    public void deleteAsset() throws Exception {
        user1 = givenUser(user1);
        Asset asset = assetRepository.save(AssetData.fileAsset());

        crt.allDeleteTests(user1, "assets:delete", "/assets", asset.getId());
    }
}
