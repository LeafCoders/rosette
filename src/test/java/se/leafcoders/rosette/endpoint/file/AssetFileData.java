package se.leafcoders.rosette.endpoint.file;

import se.leafcoders.rosette.endpoint.asset.AssetFileIn;

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
