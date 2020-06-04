package se.leafcoders.rosette.endpoint.article;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import se.leafcoders.rosette.core.exception.ApiError;
import se.leafcoders.rosette.core.exception.ForbiddenException;
import se.leafcoders.rosette.core.permission.PermissionType;
import se.leafcoders.rosette.core.persistable.HtmlContent;
import se.leafcoders.rosette.core.persistable.PersistenceService;
import se.leafcoders.rosette.endpoint.articleserie.ArticleSerieRefOut;
import se.leafcoders.rosette.endpoint.articleserie.ArticleSerieService;
import se.leafcoders.rosette.endpoint.articletype.ArticleType;
import se.leafcoders.rosette.endpoint.articletype.ArticleTypeService;
import se.leafcoders.rosette.endpoint.asset.AssetService;
import se.leafcoders.rosette.endpoint.event.EventRefOut;
import se.leafcoders.rosette.endpoint.event.EventService;
import se.leafcoders.rosette.endpoint.resource.Resource;
import se.leafcoders.rosette.endpoint.resource.ResourceRefOut;
import se.leafcoders.rosette.endpoint.resource.ResourceService;
import se.leafcoders.rosette.util.ClientServerTime;

@Service
public class ArticleService extends PersistenceService<Article, ArticleIn, ArticleOut> {

    @Autowired
    ArticleTypeService articleTypeService;

    @Autowired
    ResourceService resourceService;

    @Autowired
    ArticleSerieService articleSerieService;

    @Autowired
    EventService eventService;

    @Autowired
    AssetService assetService;

    public ArticleService(ArticleRepository repository) {
        super(Article.class, PermissionType::articles, repository);
    }

    protected ArticleRepository repo() {
        return (ArticleRepository) repository;
    }

    public List<Article> findAllOfType(Long articleTypeId, boolean checkPermissions) {
        return readManyCheckPermissions(repo().findByArticleTypeId(articleTypeId), checkPermissions);
    }

    @Override
    protected Article convertFromInDTO(ArticleIn dto, JsonNode rawIn, Article item) {
        final boolean isCreate = rawIn == null;
        // Only set when create
        if (item.getArticleTypeId() == null) {
            item.setArticleType(articleTypeService.read(dto.getArticleTypeId(), true));
        }
        if (isCreate || rawIn.has("articleSerieId")) {
            item.setArticleSerie(articleSerieService.read(dto.getArticleSerieId(), true));
            if (!isCreate) {
                articleSerieService.updateUsage(item.getArticleSerie());
            }
        }
        if (isCreate || rawIn.has("eventId")) {
            item.setEvent(eventService.read(dto.getEventId(), true));
        }
        if (isCreate || rawIn.has("time")) {
            item.setTime(dto.getTime());
        }
        if (isCreate || rawIn.has("authorIds")) {
            if (dto.getAuthorIds() != null) {
                item.setAuthors(dto.getAuthorIds().stream().map(authorId -> resourceService.read(authorId, true))
                        .collect(Collectors.toList()));
            }
        }
        if (isCreate || rawIn.has("title")) {
            item.setTitle(dto.getTitle());
        }
        if (isCreate || rawIn.has("contentRaw") || rawIn.has("contentHtml")) {
            item.setContent(new HtmlContent(dto.getContentRaw(), dto.getContentHtml()));
        }
        if (isCreate || rawIn.has("recordingId")) {
            item.setRecording(assetService.read(dto.getRecordingId(), true));
        }
        if (isCreate || rawIn.has("recordingStatus")) {
            item.setRecordingStatus(ArticleType.RecordingStatus.valueOf(dto.getRecordingStatus()));
        }
        item.setLastModifiedTime(ClientServerTime.serverTimeNow());

        // Force set recoding status if recording exist
        if (item.getRecording() != null) {
            item.setRecordingStatus(ArticleType.RecordingStatus.HAS_RECORDING);
        }
        // Force set recoding status if recording doesn't exist
        if (item.getRecording() == null && ArticleType.RecordingStatus.HAS_RECORDING.equals(item.getRecordingStatus())) {
            item.setRecordingStatus(item.getArticleType().getDefaultRecordingStatus());
        }
        return item;
    }

    @Override
    protected ArticleOut convertToOutDTO(Article item) {
        ArticleOut dto = new ArticleOut();
        dto.setId(item.getId());
        dto.setArticleTypeId(item.getArticleTypeId());
        dto.setArticleTypeIdAlias(item.getArticleType().getIdAlias());
        dto.setArticleSerie(articleSerieService.toOutRef(item.getArticleSerie(), ArticleSerieRefOut::new));
        dto.setEvent(eventService.toOutRef(item.getEvent(), EventRefOut::new));
        dto.setTime(item.getTime());
        dto.setAuthors(item.getAuthors().stream().map(author -> new ResourceRefOut(author)).collect(Collectors.toList()));
        dto.setTitle(item.getTitle());
        if (item.getContent() != null) {
            dto.setContentRaw(item.getContent().getContentRaw());
            dto.setContentHtml(item.getContent().getContentHtml());
        }
        dto.setRecording(assetService.toOut(item.getRecording()));
        dto.setRecordingStatus(item.getRecordingStatus().name());
        return dto;
    }

    public List<Resource> getAuthors(Long articleId) {
        return read(articleId, true).getAuthors();
    }

    public List<Resource> addAuthor(Long articleId, Long resourceId) {
        checkPermission(PermissionType.articles().update().forId(articleId));
        Article article = read(articleId, true);
        Resource resource = resourceService.read(resourceId, true);
        article.addAuthor(resource);
        updateResourceUsage(article);
        try {
            return repository.save(article).getAuthors();
        } catch (DataIntegrityViolationException ignore) {
            throw new ForbiddenException(ApiError.CHILD_ALREADY_EXIST);
        }
    }

    public List<Resource> removeAuthor(Long articleId, Long resourceId) {
        checkPermission(PermissionType.articles().update().forId(articleId));
        Article article = read(articleId, true);
        Resource resource = resourceService.read(resourceId, true);
        article.removeAuthor(resource);
        return repository.save(article).getAuthors();
    }

    @Override
    public Article create(ArticleIn articleIn, boolean checkPermissions) {
        Article createdArticle = super.create(articleIn, checkPermissions); 
        updateResourceUsage(createdArticle);
        articleSerieService.updateUsage(createdArticle.getArticleSerie());
        return createdArticle;
    }

    @Override
    public Article update(Long id, Class<ArticleIn> inClass, HttpServletRequest request, boolean checkPermissions) {
        Article updatedArticle = super.update(id, inClass, request, checkPermissions);
        updateResourceUsage(updatedArticle);
        return updatedArticle;
    }

    private void updateResourceUsage(Article article) {
        article.getAuthors().forEach(author -> resourceService.updateUsage(author));
    }

}
