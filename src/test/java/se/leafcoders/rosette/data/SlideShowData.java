package se.leafcoders.rosette.data;

import se.leafcoders.rosette.controller.dto.SlideShowIn;
import se.leafcoders.rosette.persistence.model.AssetFolder;
import se.leafcoders.rosette.persistence.model.SlideShow;

public class SlideShowData {

    public static SlideShow extern(AssetFolder assetFolder) {
        SlideShow slideShow = new SlideShow();
        slideShow.setIdAlias("extern");
        slideShow.setName("Extern slideshow");
        slideShow.setAssetFolder(assetFolder);
        return slideShow;
    }

    public static SlideShow intern(AssetFolder assetFolder) {
        SlideShow slideShow = new SlideShow();
        slideShow.setIdAlias("intern");
        slideShow.setName("Intern slideshow");
        slideShow.setAssetFolder(assetFolder);
        return slideShow;
    }

    public static SlideShowIn missingAllProperties() {
        return new SlideShowIn();
    }

    public static SlideShowIn invalidProperties() {
        SlideShowIn slideShow = new SlideShowIn();
        slideShow.setAssetFolderId(1234L);
        slideShow.setIdAlias("MustNotStartWithUpperCase");
        slideShow.setName("");
        return slideShow;
    }

    public static SlideShowIn newSlideShow(AssetFolder assetFolder) {
        return SlideShowData.newSlideShow("idSlideShow", "Slide show", assetFolder);
    }

    public static SlideShowIn newSlideShow(String idAlias, String name, AssetFolder assetFolder) {
        SlideShowIn slideShow = new SlideShowIn();
        slideShow.setIdAlias(idAlias);
        slideShow.setName(name);
        slideShow.setAssetFolderId(assetFolder.getId());
        return slideShow;
    }
}
