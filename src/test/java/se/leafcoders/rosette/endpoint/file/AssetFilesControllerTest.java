package se.leafcoders.rosette.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static se.leafcoders.rosette.matcher.Matchers.isValidationError;

import org.junit.Before;
import org.junit.Test;

import se.leafcoders.rosette.data.AssetFolderData;
import se.leafcoders.rosette.exception.ApiString;

public class AssetFilesControllerTest extends AbstractControllerTest {

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
