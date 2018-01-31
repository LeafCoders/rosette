package se.leafcoders.rosette.persistence.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import se.leafcoders.rosette.controller.dto.ArticleTypeIn;
import se.leafcoders.rosette.controller.dto.ArticleTypeOut;
import se.leafcoders.rosette.permission.PermissionType;
import se.leafcoders.rosette.persistence.model.ArticleType;
import se.leafcoders.rosette.persistence.repository.ArticleTypeRepository;

@Service
public class ArticleTypeService extends PersistenceService<ArticleType, ArticleTypeIn, ArticleTypeOut> {

    @Autowired
    private AssetFolderService assetFolderService;

    public ArticleTypeService(ArticleTypeRepository repository) {
        super(ArticleType.class, PermissionType.GROUPS, repository);
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
        if (rawIn == null || rawIn.has("assetFolderId")) {
            item.setAssetFolder(assetFolderService.read(dto.getAssetFolderId(), true));
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
        dto.setAssetFolder(assetFolderService.toOut(item.getAssetFolder()));
        return dto;
    }

}
