package se.leafcoders.rosette.data;

import se.leafcoders.rosette.controller.dto.AssetFolderIn;
import se.leafcoders.rosette.persistence.model.AssetFolder;

public class AssetFolderData {

    public static AssetFolder image() {
        AssetFolder assetFolder = new AssetFolder();
        assetFolder.setIdAlias("image");
        assetFolder.setName("Image");
        assetFolder.setDescription("Image files");
        assetFolder.setAllowedMimeTypes("image/");
        return assetFolder;
    }

    public static AssetFolder audio() {
        AssetFolder assetFolder = new AssetFolder();
        assetFolder.setIdAlias("audio");
        assetFolder.setName("Audio");
        assetFolder.setDescription("Audio files");
        assetFolder.setAllowedMimeTypes("audio/");
        return assetFolder;
    }
    
    public static AssetFolderIn missingAllProperties() {
        return new AssetFolderIn();
    }

    public static AssetFolderIn invalidProperties() {
        AssetFolderIn assetFolder = new AssetFolderIn();
        assetFolder.setIdAlias("Images");
        return assetFolder;
    }

    public static AssetFolderIn newAssetFolder() {
        return AssetFolderData.newAssetFolder("media", "Media files", "image/,audio/");
    }

    public static AssetFolderIn newAssetFolder(String idAlias, String name, String allowedMimeTypes) {
        AssetFolderIn assetFolder = new AssetFolderIn();
        assetFolder.setIdAlias(idAlias);
        assetFolder.setName(name);
        assetFolder.setDescription(name + " description");
        assetFolder.setAllowedMimeTypes(allowedMimeTypes);
        return assetFolder;
    }
    
}
