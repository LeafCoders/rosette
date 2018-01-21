package se.leafcoders.rosette.persistence.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import se.leafcoders.rosette.controller.dto.ArticleSerieIn;
import se.leafcoders.rosette.controller.dto.ArticleSerieOut;
import se.leafcoders.rosette.permission.PermissionType;
import se.leafcoders.rosette.persistence.model.ArticleSerie;
import se.leafcoders.rosette.persistence.repository.ArticleSerieRepository;

@Service
public class ArticleSerieService extends PersistenceService<ArticleSerie, ArticleSerieIn, ArticleSerieOut> {

    @Autowired
    UserService userService;
    
    public ArticleSerieService(ArticleSerieRepository repository) {
        super(ArticleSerie.class, PermissionType.ARTICLE_SERIES, repository);
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
            item.setArticleTypeId(dto.getArticleTypeId());
        }
        if (rawIn == null || rawIn.has("idAlias")) {
            item.setIdAlias(dto.getIdAlias());
        }
        if (rawIn == null || rawIn.has("title")) {
            item.setTitle(dto.getTitle());
        }
        if (rawIn == null || rawIn.has("content")) {
            item.setContent(dto.getContent());
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
        dto.setContent(item.getContent());
        return dto;
    }

}