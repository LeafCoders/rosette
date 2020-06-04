package se.leafcoders.rosette.endpoint.articletype;

import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import se.leafcoders.rosette.core.persistable.PersistenceService;
import se.leafcoders.rosette.endpoint.assetfolder.AssetFolderService;
import se.leafcoders.rosette.endpoint.resourcetype.ResourceTypeRefOut;
import se.leafcoders.rosette.endpoint.resourcetype.ResourceTypeService;

@Service
public class ArticleTypeService extends PersistenceService<ArticleType, ArticleTypeIn, ArticleTypeOut> {

    @Autowired
    private AssetFolderService assetFolderService;

    @Autowired
    private ResourceTypeService resourceTypeService;

    public ArticleTypeService(ArticleTypeRepository repository) {
        super(ArticleType.class, ArticleTypePermissionValue::new, repository);
    }

    @Override
    protected ArticleType convertFromInDTO(ArticleTypeIn dto, JsonNode rawIn, ArticleType item) {
        if (rawIn == null || rawIn.has("idAlias")) {
            item.setIdAlias(dto.getIdAlias());
        }
        if (rawIn == null || rawIn.has("articlesTitle")) {
            item.setArticlesTitle(dto.getArticlesTitle());
        }
        if (rawIn == null || rawIn.has("newArticleTitle")) {
            item.setNewArticleTitle(dto.getNewArticleTitle());
        }
        if (rawIn == null || rawIn.has("articleSeriesTitle")) {
            item.setArticleSeriesTitle(dto.getArticleSeriesTitle());
        }
        if (rawIn == null || rawIn.has("newArticleSerieTitle")) {
            item.setNewArticleSerieTitle(dto.getNewArticleSerieTitle());
        }
        if (rawIn == null || rawIn.has("imageFolderId")) {
            item.setImageFolder(assetFolderService.read(dto.getImageFolderId(), true));
        }
        if (rawIn == null || rawIn.has("recordingFolderId")) {
            item.setRecordingFolder(assetFolderService.read(dto.getRecordingFolderId(), true));
        }
        if (rawIn == null || rawIn.has("defaultRecordingStatus")) {
            item.setDefaultRecordingStatus(ArticleType.RecordingStatus.valueOf(dto.getDefaultRecordingStatus()));
        }
        if (rawIn == null || rawIn.has("authorResourceTypeId")) {
            item.setAuthorResourceType(resourceTypeService.read(dto.getAuthorResourceTypeId(), true));
        }
        return item;
    }

    @Override
    protected ArticleTypeOut convertToOutDTO(ArticleType item) {
        ArticleTypeOut dto = new ArticleTypeOut();
        dto.setId(item.getId());
        dto.setIdAlias(item.getIdAlias());
        dto.setArticlesTitle(item.getArticlesTitle());
        dto.setNewArticleTitle(item.getNewArticleTitle());
        dto.setArticleSeriesTitle(item.getArticleSeriesTitle());
        dto.setNewArticleSerieTitle(item.getNewArticleSerieTitle());
        dto.setImageFolder(assetFolderService.toOut(item.getImageFolder()));
        dto.setRecordingFolder(assetFolderService.toOut(item.getRecordingFolder()));
        dto.setDefaultRecordingStatus(item.getDefaultRecordingStatus().name());
        dto.setAuthorResourceType(resourceTypeService.toOutRef(item.getAuthorResourceType(), ResourceTypeRefOut::new));
        return dto;
    }

}
