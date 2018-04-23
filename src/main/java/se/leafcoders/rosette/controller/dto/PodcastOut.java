package se.leafcoders.rosette.controller.dto;

public class PodcastOut {

    private Long id;
    private ArticleTypeRefOut articleType;
    private String idAlias;
    private String title;
    private String subTitle;
    private String authorName;
    private String copyright;
    protected String description;
    protected String mainCategory;
    protected String subCategory;
    protected String language;
    protected String link;
    private AssetOut image;


    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ArticleTypeRefOut getArticleType() {
        return articleType;
    }

    public void setArticleType(ArticleTypeRefOut articleType) {
        this.articleType = articleType;
    }

    public String getIdAlias() {
        return idAlias;
    }

    public void setIdAlias(String idAlias) {
        this.idAlias = idAlias;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMainCategory() {
        return mainCategory;
    }

    public void setMainCategory(String mainCategory) {
        this.mainCategory = mainCategory;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public AssetOut getImage() {
        return image;
    }

    public void setImage(AssetOut image) {
        this.image = image;
    }

}
