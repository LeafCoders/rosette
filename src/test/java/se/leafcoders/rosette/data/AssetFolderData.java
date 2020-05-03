package se.leafcoders.rosette.data;

import se.leafcoders.rosette.controller.dto.AssetFolderIn;

public class AssetFolderData {

    public static AssetFolderIn image() {
        AssetFolderIn assetFolder = new AssetFolderIn();
        assetFolder.setIdAlias("image");
        assetFolder.setName("Image");
        assetFolder.setDescription("Image files");
        assetFolder.setAllowedMimeTypes("image/");
        return assetFolder;
    }

    public static AssetFolderIn audio() {
        AssetFolderIn assetFolder = new AssetFolderIn();
        assetFolder.setIdAlias("audio");
        assetFolder.setName("Audio");
        assetFolder.setDescription("Audio files");
        assetFolder.setAllowedMimeTypes("audio/");
        return assetFolder;
    }

    public static AssetFolderIn staticFileKey() {
        AssetFolderIn assetFolder = new AssetFolderIn();
        assetFolder.setIdAlias("staticFileKey");
        assetFolder.setName("Static file path");
        assetFolder.setDescription("Any files");
        assetFolder.setAllowedMimeTypes("");
        assetFolder.setStaticFileKey(true);
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

    public static AssetFolderIn newAssetFolderForStaticFileKey(String idAlias, String name, String allowedMimeTypes) {
        AssetFolderIn assetFolder = newAssetFolder(idAlias, name, allowedMimeTypes);
        assetFolder.setStaticFileKey(true);
        return assetFolder;
    }

}
