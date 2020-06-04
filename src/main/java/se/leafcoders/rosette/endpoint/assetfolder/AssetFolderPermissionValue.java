package se.leafcoders.rosette.endpoint.assetfolder;

import se.leafcoders.rosette.core.permission.PermissionValue;

public class AssetFolderPermissionValue extends PermissionValue {

    private static final String ACTION_MANAGE_ASSETS = "manageAssets";

    public AssetFolderPermissionValue() {
        super("assetFolders");
    }

    public AssetFolderPermissionValue manageAssets() {
        return withAction(ACTION_MANAGE_ASSETS);
    }
}
