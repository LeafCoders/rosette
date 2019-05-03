package se.leafcoders.rosette.persistence.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import se.leafcoders.rosette.controller.dto.ArticleSerieIn;
import se.leafcoders.rosette.controller.dto.ArticleSerieOut;
import se.leafcoders.rosette.permission.PermissionType;
import se.leafcoders.rosette.persistence.model.ArticleSerie;
import se.leafcoders.rosette.persistence.model.HtmlContent;
import se.leafcoders.rosette.persistence.repository.ArticleSerieRepository;

@Service
public class ArticleSerieService extends PersistenceService<ArticleSerie, ArticleSerieIn, ArticleSerieOut> {

    @Autowired
    private ArticleTypeService articleTypeService;
    
    @Autowired
    private AssetService assetService;
    
    public ArticleSerieService(ArticleSerieRepository repository) {
        super(ArticleSerie.class, PermissionType::articleSeries, repository);
    }

    protected ArticleSerieRepository repo() {
        return (ArticleSerieRepository) repository;
    }

    public List<ArticleSerie> findAllOfType(Long articleTypeId, boolean checkPermissions) {
        return readManyCheckPermissions(repo().findByArticleTypeId(articleTypeId), checkPermissions);
    }
    
    @Override
    protected ArticleSerie convertFromInDTO(ArticleSerieIn dto, JsonNode rawIn, ArticleSerie item) {
        // Only set when create
        if (item.getArticleTypeId() == null) {
            item.setArticleType(articleTypeService.read(dto.getArticleTypeId(), true));
        }
        if (rawIn == null || rawIn.has("idAlias")) {
            item.setIdAlias(dto.getIdAlias());
        }
        if (rawIn == null || rawIn.has("title")) {
            item.setTitle(dto.getTitle());
        }
        if (rawIn == null || rawIn.has("contentRaw") || rawIn.has("contentHtml")) {
            item.setContent(new HtmlContent(dto.getContentRaw(), dto.getContentHtml()));
        }
        if (rawIn == null || rawIn.has("imageId")) {
            item.setImage(assetService.read(dto.getImageId(), true));
        }
        return item;
    }

    @Override
    protected ArticleSerieOut convertToOutDTO(ArticleSerie item) {
        ArticleSerieOut dto = new ArticleSerieOut();
        dto.setId(item.getId());
        dto.setIdAlias(item.getIdAlias());
        dto.setArticleTypeId(item.getArticleTypeId());
        dto.setTitle(item.getTitle());
        if (item.getContent() != null) {
            dto.setContentRaw(item.getContent().getContentRaw());
            dto.setContentHtml(item.getContent().getContentHtml());
        }
        dto.setImage(assetService.toOut(item.getImage()));
        return dto;
    }

}
