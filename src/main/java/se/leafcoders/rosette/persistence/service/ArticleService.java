package se.leafcoders.rosette.persistence.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import se.leafcoders.rosette.controller.dto.ArticleIn;
import se.leafcoders.rosette.controller.dto.ArticleOut;
import se.leafcoders.rosette.controller.dto.ArticleSerieRefOut;
import se.leafcoders.rosette.controller.dto.ResourceRefOut;
import se.leafcoders.rosette.exception.ApiError;
import se.leafcoders.rosette.exception.ForbiddenException;
import se.leafcoders.rosette.permission.PermissionAction;
import se.leafcoders.rosette.permission.PermissionType;
import se.leafcoders.rosette.persistence.model.Article;
import se.leafcoders.rosette.persistence.model.Resource;
import se.leafcoders.rosette.persistence.repository.ArticleRepository;

@Service
public class ArticleService extends PersistenceService<Article, ArticleIn, ArticleOut> {

    @Autowired
    UserService userService;

    @Autowired
    ResourceService resourceService;
    
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
        if (rawIn == null || rawIn.has("authorIds")) {
            item.setAuthors(dto.getAuthorIds().stream().map(authorId -> resourceService.read(authorId, true)).collect(Collectors.toList()));
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
        dto.setAuthors(item.getAuthors().stream().map(author -> new ResourceRefOut(author)).collect(Collectors.toList()));
        dto.setTitle(item.getTitle());
        dto.setContent(item.getContent());
        return dto;
    }

    public List<Resource> getAuthors(Long articleId) {
        return read(articleId, true).getAuthors();
    }

    public List<Resource> addAuthor(Long articleId, Long resourceId) {
        checkPermission(permissionValue(PermissionAction.UPDATE).forId(articleId));
        Article article = read(articleId, true);
        Resource resource = resourceService.read(resourceId, true);
        article.addAuthor(resource);
        try {
            return repository.save(article).getAuthors();
        } catch (DataIntegrityViolationException ignore) {
            throw new ForbiddenException(ApiError.CHILD_ALREADY_EXIST);
        }
    }

    public List<Resource> removeAuthor(Long articleId, Long resourceId) {
        checkPermission(permissionValue(PermissionAction.UPDATE).forId(articleId));
        Article article = read(articleId, true);
        Resource resource = resourceService.read(resourceId, true);
        article.removeAuthor(resource);
        return repository.save(article).getAuthors();
    }

}