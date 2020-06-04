package se.leafcoders.rosette.endpoint.asset;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasKey;
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
import se.leafcoders.rosette.endpoint.assetfolder.AssetFolder;
import se.leafcoders.rosette.endpoint.assetfolder.AssetFolderData;

public class AssetControllerTest extends AbstractControllerTest {

    @Autowired
    private AssetRepository assetRepository;

    private final CommonRequestTests crt = new CommonRequestTests(this, Asset.class);

    @Before
    public void setup() throws Exception {
        super.setup();
    }

    @Test
    public void getAsset() throws Exception {
        user1 = givenUser(user1);
        final AssetFolder assetFolder = givenAssetFolder(AssetFolderData.image());
        final Asset asset = assetRepository.save(AssetData.fileAsset(assetFolder.getId()));

        crt.allGetOneTests(user1, "assets:read", "/api/assets", asset.getId())
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
        final AssetFolder assetFolder = givenAssetFolder(AssetFolderData.image());
        final Asset asset1 = assetRepository.save(AssetData.fileAsset(assetFolder.getId()));
        final Asset asset2 = assetRepository.save(AssetData.fileAsset(assetFolder.getId()));

        crt.allGetManyTests(user1, "assets:read", "/api/assets?assetFolderId=" + assetFolder.getId())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", anyOf(isIdOf(asset1), isIdOf(asset2))))
                .andExpect(jsonPath("$[1].id", anyOf(isIdOf(asset1), isIdOf(asset2))));
    }

    @Test
    public void createAsset() throws Exception {
        user1 = givenUser(user1);
        final AssetFolder assetFolder = givenAssetFolder(AssetFolderData.image());
        final AssetIn asset = AssetData.newUrlAsset(assetFolder.getId());

        crt.allPostTests(user1, "assets:create", "/api/assets", json(asset))
                .andExpect(jsonPath("$.type", is(asset.getType())))
                .andExpect(
                        jsonPath("$", allOf(hasKey("id"), hasKey("mimeType"), hasKey("fileName"), hasKey("fileSize"))));

        // Check missing properties
        crt.postExpectBadRequest(user1, "/api/assets", json(AssetData.missingAllProperties()))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]", isValidationError("folderId", ApiString.NOT_NULL)))
                .andExpect(jsonPath("$[1]", isValidationError("type", ApiString.STRING_NOT_EMPTY)));

        // Check invalid properties
        crt.postExpectBadRequest(user1, "/api/assets", json(AssetData.invalidProperties()))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0]", isValidationError("type", ApiString.STRING_NOT_ANY_OF)));
    }

    @Test
    public void updateAsset() throws Exception {
        user1 = givenUser(user1);
        final AssetFolder assetFolder = givenAssetFolder(AssetFolderData.image());
        final Asset asset = assetRepository.save(AssetData.urlAsset(assetFolder.getId()));

        String jsonData = mapToJson(data -> data.put("url", "http://new.url"));

        crt.allPutTests(user1, "assets:update", "/api/assets", asset.getId(), jsonData)
                .andExpect(jsonPath("$.url", is("http://new.url")));
    }

    @Test
    public void deleteAsset() throws Exception {
        user1 = givenUser(user1);
        final AssetFolder assetFolder = givenAssetFolder(AssetFolderData.image());
        final Asset asset = assetRepository.save(AssetData.fileAsset(assetFolder.getId()));

        crt.allDeleteTests(user1, "assets:delete", "/api/assets", asset.getId());
    }
}
