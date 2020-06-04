package se.leafcoders.rosette.endpoint.podcast;

import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import se.leafcoders.rosette.core.persistable.PersistenceService;
import se.leafcoders.rosette.endpoint.articletype.ArticleTypeRefOut;
import se.leafcoders.rosette.endpoint.articletype.ArticleTypeService;
import se.leafcoders.rosette.endpoint.asset.AssetService;

@Service
public class PodcastService extends PersistenceService<Podcast, PodcastIn, PodcastOut> {

    @Autowired
    private ArticleTypeService articleTypeService;

    @Autowired
    private AssetService assetService;
    
    public PodcastService(PodcastRepository repository) {
        super(Podcast.class, PodcastPermissionValue::new, repository);
    }
    
    private PodcastRepository repo() {
        return (PodcastRepository) repository;
    }

    @Override
    protected Podcast convertFromInDTO(PodcastIn dto, JsonNode rawIn, Podcast item) {
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
        if (rawIn == null || rawIn.has("subTitle")) {
            item.setSubTitle(dto.getSubTitle());
        }
        if (rawIn == null || rawIn.has("authorName")) {
            item.setAuthorName(dto.getAuthorName());
        }
        if (rawIn == null || rawIn.has("authorEmail")) {
            item.setAuthorEmail(dto.getAuthorEmail());
        }
        if (rawIn == null || rawIn.has("copyright")) {
            item.setCopyright(dto.getCopyright());
        }        
        if (rawIn == null || rawIn.has("description")) {
            item.setDescription(dto.getDescription());
        }
        if (rawIn == null || rawIn.has("mainCategory")) {
            item.setMainCategory(dto.getMainCategory());
        }
        if (rawIn == null || rawIn.has("subCategory")) {
            item.setSubCategory(dto.getSubCategory());
        }
        if (rawIn == null || rawIn.has("language")) {
            item.setLanguage(dto.getLanguage());
        }
        if (rawIn == null || rawIn.has("authorLink")) {
            item.setAuthorLink(dto.getAuthorLink());
        }
        if (rawIn == null || rawIn.has("articlesLink")) {
            item.setArticlesLink(dto.getArticlesLink());
        }
        if (rawIn == null || rawIn.has("imageId")) {
            item.setImage(assetService.read(dto.getImageId(), true));
        }
        return item;
    }

    @Override
    protected PodcastOut convertToOutDTO(Podcast item) {
        PodcastOut dto = new PodcastOut();
        dto.setIdAlias(item.getIdAlias());
        dto.setId(item.getId());
        dto.setArticleType(new ArticleTypeRefOut(item.getArticleType()));
        dto.setTitle(item.getTitle());
        dto.setSubTitle(item.getSubTitle());
        dto.setAuthorName(item.getAuthorName());
        dto.setAuthorEmail(item.getAuthorEmail());
        dto.setCopyright(item.getCopyright());
        dto.setDescription(item.getDescription());
        dto.setMainCategory(item.getMainCategory());
        dto.setSubCategory(item.getSubCategory());
        dto.setLanguage(item.getLanguage());
        dto.setAuthorLink(item.getAuthorLink());
        dto.setArticlesLink(item.getArticlesLink());
        dto.setImage(assetService.toOut(item.getImage()));
        return dto;
    }
    
    public Podcast findByIdAlias(String idAlias) {
        return repo().findOneByIdAlias(idAlias);
    }
}
