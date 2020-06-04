package se.leafcoders.rosette.endpoint.articletype;

import lombok.Data;
import se.leafcoders.rosette.endpoint.assetfolder.AssetFolderOut;
import se.leafcoders.rosette.endpoint.resourcetype.ResourceTypeRefOut;

@Data
public class ArticleTypeOut {

    private Long id;
    private String idAlias;
    private String articlesTitle;
    private String newArticleTitle;
    private String articleSeriesTitle;
    private String newArticleSerieTitle;
    private AssetFolderOut imageFolder;
    private AssetFolderOut recordingFolder;
    private String defaultRecordingStatus;
    private ResourceTypeRefOut authorResourceType;
}
