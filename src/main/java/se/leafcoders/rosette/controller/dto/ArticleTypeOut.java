package se.leafcoders.rosette.controller.dto;

import lombok.Data;

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
