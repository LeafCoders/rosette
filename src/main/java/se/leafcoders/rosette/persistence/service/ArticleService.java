package se.leafcoders.rosette.persistence.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import se.leafcoders.rosette.controller.dto.ArticleIn;
import se.leafcoders.rosette.controller.dto.ArticleOut;
import se.leafcoders.rosette.controller.dto.ArticleSerieRefOut;
import se.leafcoders.rosette.permission.PermissionType;
import se.leafcoders.rosette.persistence.model.Article;
import se.leafcoders.rosette.persistence.model.ArticleSerie;
import se.leafcoders.rosette.persistence.repository.ArticleRepository;
import se.leafcoders.rosette.persistence.repository.ArticleSerieRepository;

@Service
public class ArticleService extends PersistenceService<Article, ArticleIn, ArticleOut> {

    @Autowired
    UserService userService;

    @Autowired
    ArticleSerieService articleSerieService;

    public ArticleService(ArticleRepository repository) {
        super(Article.class, PermissionType.ARTICLES, repository);
    }

    protected ArticleRepository repo() {
        return (ArticleRepository) repository;
    }

    public List<Article> findAllOfType(Long articleTypeId, boolean checkPermissions) {
        return readManyCheckPermissions(repo().findByArticleTypeId(articleTypeId), checkPermissions);
    }
    
    @Override
    protected Article convertFromInDTO(ArticleIn dto, JsonNode rawIn, Article item) {
        // Only set when create
        if (item.getArticleTypeId() == null) {
            item.setArticleTypeId(dto.getArticleTypeId());
        }
        if (rawIn == null || rawIn.has("articleSerieId")) {
            item.setArticleSerie(articleSerieService.read(dto.getArticleSerieId(), true));
        }
        if (rawIn == null || rawIn.has("time")) {
            item.setTime(dto.getTime());
        }
        if (rawIn == null || rawIn.has("title")) {
            item.setTitle(dto.getTitle());
        }
        if (rawIn == null || rawIn.has("content")) {
            item.setContent(dto.getContent());
        }
        item.setLastModifiedTime(LocalDateTime.now());
        return item;
    }

    @Override
    protected ArticleOut convertToOutDTO(Article item) {
        ArticleOut dto = new ArticleOut();
        dto.setId(item.getId());
        dto.setArticleTypeId(item.getArticleTypeId());
        dto.setArticleSerie(new ArticleSerieRefOut(item.getArticleSerie()));
        dto.setTime(item.getTime());
        dto.setTitle(item.getTitle());
        dto.setContent(item.getContent());
        return dto;
    }

}
