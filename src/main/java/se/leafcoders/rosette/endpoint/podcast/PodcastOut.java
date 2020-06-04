package se.leafcoders.rosette.endpoint.podcast;

import lombok.Data;
import se.leafcoders.rosette.endpoint.articletype.ArticleTypeRefOut;
import se.leafcoders.rosette.endpoint.asset.AssetOut;

@Data
public class PodcastOut {

    private Long id;
    private ArticleTypeRefOut articleType;
    private String idAlias;
    private String title;
    private String subTitle;
    private String authorName;
    private String authorEmail;
    private String authorLink;
    private String copyright;
    private String description;
    private String mainCategory;
    private String subCategory;
    private String language;
    private String articlesLink;
    private AssetOut image;
}
