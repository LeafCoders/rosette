package se.leafcoders.rosette.endpoint.file;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static se.leafcoders.rosette.test.matcher.Matchers.isValidationError;

import org.junit.Before;
import org.junit.Test;

import se.leafcoders.rosette.core.exception.ApiString;
import se.leafcoders.rosette.endpoint.AbstractControllerTest;
import se.leafcoders.rosette.endpoint.assetfolder.AssetFolderData;

public class AssetFileControllerTest extends AbstractControllerTest {

    @Before
    public void setup() throws Exception {
        super.setup();
    }

    @Test
    public void createAssetFile() throws Exception {
        givenUser(user1);
        final Long folderId = givenAssetFolder(AssetFolderData.image()).getId();

        // Without permission
        withUser(user1, uploadFile(folderId, "image.png", "someImage.png", "image/png"))
                .andExpect(status().isForbidden());

        givenPermissionForUser(user1, "assets:create");

        // Fail with unknown folder
        withUser(user1, uploadFile(123456789L, "image.png", "someImage.png", "image/png"))
                .andExpect(status().isNotFound());

        // Fail with invalid parameters
        withUser(user1, uploadFile(folderId, "image.png", "../no/dont/try/that.png", "image/png"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0]", isValidationError("fileName", ApiString.FILENAME_INVALID)));

        // Success
        withUser(user1, uploadFile(folderId, "image.png", "someImage.png", "image/png"))
                .andExpect(status().isCreated());

        // Success with duplicate filename
        withUser(user1, uploadFile(folderId, "image.png", "someImage.png", "image/png"))
                .andExpect(status().isCreated());
    }

    @Test
    public void createAssetFileForStaticFileKey() throws Exception {
        givenUser(user1);
        givenPermissionForUser(user1, "assets:create");
        final Long folderId = givenAssetFolder(AssetFolderData.staticFileKey()).getId();

        // Success
        withUser(user1, uploadFile(folderId, "image.png", "someImage.png", "image/png"))
                .andExpect(status().isCreated());

        // Fail with duplicate filename
        withUser(user1, uploadFile(folderId, "image.png", "someImage.png", "image/png"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0]", isValidationError("file", ApiString.FILENAME_NOT_UNIQUE)));
    }

}
