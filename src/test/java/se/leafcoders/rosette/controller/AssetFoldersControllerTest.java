package se.leafcoders.rosette.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static se.leafcoders.rosette.matcher.Matchers.isIdOf;
import static se.leafcoders.rosette.matcher.Matchers.isValidationError;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import se.leafcoders.rosette.controller.dto.AssetFolderIn;
import se.leafcoders.rosette.data.AssetFolderData;
import se.leafcoders.rosette.exception.ApiString;
import se.leafcoders.rosette.persistence.model.AssetFolder;
import se.leafcoders.rosette.persistence.repository.AssetFolderRepository;

public class AssetFoldersControllerTest extends AbstractControllerTest {

    @Autowired
    private AssetFolderRepository assetFolderRepository;

    private CommonRequestTests crt = new CommonRequestTests(this, AssetFolder.class);

    @Before
    public void setup() throws Exception {
        super.setup();
    }

    @Test
    public void getAssetFolder() throws Exception {
        user1 = givenUser(user1);
        AssetFolder assetFolder = assetFolderRepository.save(AssetFolderData.image());

        crt.allGetOneTests(user1, "assetFolders:read", "/assetFolders", assetFolder.getId())
            .andExpect(jsonPath("$.idAlias", is(assetFolder.getIdAlias())))
            .andExpect(jsonPath("$.name", is(assetFolder.getName())))
            .andExpect(jsonPath("$.description", is(assetFolder.getDescription())))
            .andExpect(jsonPath("$.allowedMimeTypes", is(assetFolder.getAllowedMimeTypes())));
    }

    @Test
    public void getAssetFolders() throws Exception {
        user1 = givenUser(user1);
        AssetFolder assetFolder1 = assetFolderRepository.save(AssetFolderData.image());
        AssetFolder assetFolder2 = assetFolderRepository.save(AssetFolderData.audio());

        crt.allGetManyTests(user1, "assetFolders:read", "/assetFolders")
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id", isIdOf(assetFolder1)))
            .andExpect(jsonPath("$[1].id", isIdOf(assetFolder2)));
    }

    @Test
    public void createAssetFolder() throws Exception {
        user1 = givenUser(user1);
        AssetFolderIn assetFolder = AssetFolderData.newAssetFolder();

        crt.allPostTests(user1, "assetFolders:create", "/assetFolders", json(assetFolder))
            .andExpect(jsonPath("$.idAlias", is(assetFolder.getIdAlias())))
            .andExpect(jsonPath("$.name", is(assetFolder.getName())))
            .andExpect(jsonPath("$.description", is(assetFolder.getDescription())))
            .andExpect(jsonPath("$.allowedMimeTypes", is(assetFolder.getAllowedMimeTypes())));

        // Check missing properties
        crt.postExpectBadRequest(user1, "/assetFolders", json(AssetFolderData.missingAllProperties()))
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0]", isValidationError("idAlias", ApiString.STRING_NOT_EMPTY)))
            .andExpect(jsonPath("$[1]", isValidationError("name", ApiString.STRING_NOT_EMPTY)));

        // Check invalid properties
        crt.postExpectBadRequest(user1, "/assetFolders", json(AssetFolderData.invalidProperties()))
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0]", isValidationError("idAlias", ApiString.IDALIAS_INVALID_FORMAT)))
            .andExpect(jsonPath("$[1]", isValidationError("name", ApiString.STRING_NOT_EMPTY)));
    }

    @Test
    public void updateAssetFolder() throws Exception {
        user1 = givenUser(user1);
        AssetFolder assetFolder = assetFolderRepository.save(AssetFolderData.image());

        String jsonData = mapToJson(
            data -> {
                data.put("name", "New image");
                data.put("description", "New image description");
                data.put("allowedMimeTypes", "image/png");
                return data;
            }
        );

        crt.allPutTests(user1, "assetFolders:update", "/assetFolders", assetFolder.getId(), jsonData)
            .andExpect(jsonPath("$.idAlias", is(assetFolder.getIdAlias())))
            .andExpect(jsonPath("$.name", is("New image")))
            .andExpect(jsonPath("$.description", is("New image description")))
            .andExpect(jsonPath("$.allowedMimeTypes", is("image/png")));
    }

    @Test
    public void deleteAssetFolder() throws Exception {
        user1 = givenUser(user1);
        AssetFolder assetFolder = assetFolderRepository.save(AssetFolderData.image());

        crt.allDeleteTests(user1, "assetFolders:delete", "/assetFolders", assetFolder.getId());
    }
}
