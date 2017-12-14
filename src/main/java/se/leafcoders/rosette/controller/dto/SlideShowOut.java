package se.leafcoders.rosette.controller.dto;

import java.util.List;

public class SlideShowOut {

    private Long id;
    private String idAlias;
    private String name;
    private AssetFolderOut assetFolder;
    private List<SlideOut> slides;

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdAlias() {
        return idAlias;
    }

    public void setIdAlias(String idAlias) {
        this.idAlias = idAlias;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AssetFolderOut getAssetFolder() {
        return assetFolder;
    }

    public void setAssetFolder(AssetFolderOut assetFolder) {
        this.assetFolder = assetFolder;
    }

    public List<SlideOut> getSlides() {
        return slides;
    }

    public void setSlides(List<SlideOut> slides) {
        this.slides = slides;
    }
}
