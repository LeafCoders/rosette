package se.leafcoders.rosette.data;

import se.leafcoders.rosette.controller.dto.AssetFileIn;

public class AssetFileData {

    public static AssetFileIn missingAllProperties() {
        return new AssetFileIn();
    }

    public static AssetFileIn invalidProperties() {
        AssetFileIn assetFile = new AssetFileIn();
        assetFile.setFileName("../notAllowed.txt");
        return assetFile;
    }

    public static AssetFileIn newAssetFile(Long folderId, String fileName) {
        AssetFileIn assetFile = new AssetFileIn();
        assetFile.setFolderId(folderId);
        assetFile.setFileName(fileName);
        return assetFile;
    }
    
}
