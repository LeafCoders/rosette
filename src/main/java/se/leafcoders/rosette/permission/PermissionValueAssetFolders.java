package se.leafcoders.rosette.permission;

public class PermissionValueAssetFolders extends PermissionValue {

    private static final String ACTION_MANAGE_ASSETS = "manageAssets";

    public PermissionValueAssetFolders() {
        super("assetFolders");
    }
    
    public PermissionValueAssetFolders manageAssets() {
        return withAction(ACTION_MANAGE_ASSETS);
    }
}
