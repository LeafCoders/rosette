package se.leafcoders.rosette.endpoint.articletype;

import se.leafcoders.rosette.endpoint.assetfolder.AssetFolder;
import se.leafcoders.rosette.endpoint.resourcetype.ResourceType;

public class ArticleTypeData {

    public static ArticleType existingArticleType(String name, ResourceType authorResourceType, AssetFolder imageFolder, AssetFolder recordingFolder) {
        ArticleType articleType = new ArticleType();
        articleType.setIdAlias(name.toLowerCase());
        articleType.setArticlesTitle(name);
        articleType.setNewArticleTitle("Ny " + name);
        articleType.setArticleSeriesTitle(name + "-serie");
        articleType.setNewArticleSerieTitle("Ny " + name + "-serie");
        articleType.setAuthorResourceType(authorResourceType);
        articleType.setImageFolder(imageFolder);
        articleType.setRecordingFolder(recordingFolder);
        articleType.setDefaultRecordingStatus(ArticleType.RecordingStatus.EXPECTING_RECORDING);
        return articleType;
    }

    public static ArticleTypeIn missingAllProperties() {
        return new ArticleTypeIn();
    }

    public static ArticleTypeIn invalidProperties() {
        ArticleTypeIn articleType = new ArticleTypeIn();
        // TODO
        return articleType;
    }

    public static ArticleTypeIn newArticleType(String name, Long authorResourceTypeId, Long imageFolderId, Long recordingFolderId) {
        ArticleTypeIn articleType = new ArticleTypeIn();
        articleType.setIdAlias(name.toLowerCase());
        articleType.setArticlesTitle(name);
        articleType.setNewArticleTitle("Ny " + name);
        articleType.setArticleSeriesTitle(name + "-serie");
        articleType.setNewArticleSerieTitle("Ny " + name + "-serie");
        articleType.setAuthorResourceTypeId(authorResourceTypeId);
        articleType.setImageFolderId(imageFolderId);
        articleType.setRecordingFolderId(recordingFolderId);
        articleType.setDefaultRecordingStatus(ArticleType.RecordingStatus.EXPECTING_RECORDING.name());
        return articleType;
    }
    
}
