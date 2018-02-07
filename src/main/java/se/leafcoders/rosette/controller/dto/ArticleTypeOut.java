package se.leafcoders.rosette.controller.dto;

public class ArticleTypeOut {

    private Long id;
    private String idAlias;
    private String articlesTitle;
    private String newArticleTitle;
    private String articleSeriesTitle;
    private String newArticleSerieTitle;
    private AssetFolderOut imageFolder;
    private AssetFolderOut recordingFolder;
    private ResourceTypeRefOut authorResourceType;

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

    public String getArticlesTitle() {
        return articlesTitle;
    }

    public void setArticlesTitle(String articlesTitle) {
        this.articlesTitle = articlesTitle;
    }

    public String getNewArticleTitle() {
        return newArticleTitle;
    }

    public void setNewArticleTitle(String newArticleTitle) {
        this.newArticleTitle = newArticleTitle;
    }

    public String getArticleSeriesTitle() {
        return articleSeriesTitle;
    }

    public void setArticleSeriesTitle(String articleSeriesTitle) {
        this.articleSeriesTitle = articleSeriesTitle;
    }

    public String getNewArticleSerieTitle() {
        return newArticleSerieTitle;
    }

    public void setNewArticleSerieTitle(String newArticleSerieTitle) {
        this.newArticleSerieTitle = newArticleSerieTitle;
    }

    public AssetFolderOut getImageFolder() {
        return imageFolder;
    }

    public void setImageFolder(AssetFolderOut imageFolder) {
        this.imageFolder = imageFolder;
    }

    public AssetFolderOut getRecordingFolder() {
        return recordingFolder;
    }

    public void setRecordingFolder(AssetFolderOut recordingFolder) {
        this.recordingFolder = recordingFolder;
    }

    public ResourceTypeRefOut getAuthorResourceType() {
        return authorResourceType;
    }

    public void setAuthorResourceType(ResourceTypeRefOut authorResourceType) {
        this.authorResourceType = authorResourceType;
    }

}
