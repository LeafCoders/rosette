package se.leafcoders.rosette.data;

import se.leafcoders.rosette.controller.dto.AssetIn;
import se.leafcoders.rosette.persistence.model.Asset;

public class AssetData {

    public static Asset fileAsset() {
        Asset asset = new Asset();
        asset.setType(Asset.AssetType.FILE);
        asset.setMimeType("image/jpeg");
        asset.setFileName("image.jpg");
        asset.setFileSize(1024L);
        asset.setWidth(100L);
        asset.setHeight(100L);
        return asset;
    }

    public static AssetIn missingAllProperties() {
        return new AssetIn();
    }

    public static AssetIn invalidProperties() {
        AssetIn asset = new AssetIn();
        asset.setType("tomato");
        return asset;
    }

    public static AssetIn newUrlAsset() {
        return AssetData.newUrlAsset("http://placehold.it/10x10");
    }

    public static AssetIn newUrlAsset(String url) {
        AssetIn asset = new AssetIn();
        asset.setType("URL");
        asset.setUrl(url);
        return asset;
    }
    
}
