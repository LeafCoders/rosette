package se.leafcoders.rosette.data;

import se.leafcoders.rosette.controller.dto.AssetIn;
import se.leafcoders.rosette.persistence.model.Asset;

public class AssetData {

    public static Asset fileAsset(long folderId) {
        Asset asset = new Asset();
        asset.setFolderId(folderId);
        asset.setFileId(String.format("%06.0f", 1_000_000 * Math.random()));
        asset.setType(Asset.AssetType.FILE);
        asset.setMimeType("image/jpeg");
        asset.setFileName("image.jpg");
        asset.setFileSize(1024L);
        asset.setWidth(100L);
        asset.setHeight(100L);
        return asset;
    }

    public static Asset urlAsset(long folderId) {
        Asset asset = new Asset();
        asset.setFolderId(folderId);
        asset.setFileId(String.format("%06.0f", 1_000_000 * Math.random()));
        asset.setType(Asset.AssetType.URL);
        asset.setMimeType("application/url");
        asset.setUrl("http://placehold.it/16x16");
        return asset;
    }

    public static AssetIn missingAllProperties() {
        return new AssetIn();
    }

    public static AssetIn invalidProperties() {
        AssetIn asset = new AssetIn();
        asset.setFolderId(999999L);
        asset.setType("tomato");
        return asset;
    }

    public static AssetIn newUrlAsset(long folderId) {
        return AssetData.newUrlAsset(folderId, "http://placehold.it/10x10");
    }

    public static AssetIn newUrlAsset(long folderId, String url) {
        AssetIn asset = new AssetIn();
        asset.setFolderId(folderId);
        asset.setType("URL");
        asset.setUrl(url);
        return asset;
    }

}
