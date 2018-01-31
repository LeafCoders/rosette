package se.leafcoders.rosette.persistence.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import se.leafcoders.rosette.controller.dto.PodcastIn;
import se.leafcoders.rosette.controller.dto.PodcastOut;
import se.leafcoders.rosette.permission.PermissionType;
import se.leafcoders.rosette.persistence.model.Podcast;
import se.leafcoders.rosette.persistence.repository.PodcastRepository;

@Service
public class PodcastService extends PersistenceService<Podcast, PodcastIn, PodcastOut> {

    @Autowired
    private AssetService assetService;

    public PodcastService(PodcastRepository repository) {
        super(Podcast.class, PermissionType.PODCASTS, repository);
    }

    @Override
    protected Podcast convertFromInDTO(PodcastIn dto, JsonNode rawIn, Podcast item) {
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
        if (rawIn == null || rawIn.has("subTitle")) {
            item.setSubTitle(dto.getSubTitle());
        }
        if (rawIn == null || rawIn.has("authorName")) {
            item.setAuthorName(dto.getAuthorName());
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
        if (rawIn == null || rawIn.has("link")) {
            item.setLink(dto.getLink());
        }
        if (rawIn == null || rawIn.has("image")) {
            item.setImage(assetService.read(dto.getImageId(), true));
        }
        return item;
    }

    @Override
    protected PodcastOut convertToOutDTO(Podcast item) {
        PodcastOut dto = new PodcastOut();
        dto.setIdAlias(item.getIdAlias());
        dto.setId(item.getId());
        dto.setArticleTypeId(item.getArticleTypeId());
        dto.setTitle(item.getTitle());
        dto.setSubTitle(item.getSubTitle());
        dto.setAuthorName(item.getAuthorName());
        dto.setCopyright(item.getCopyright());
        dto.setDescription(item.getDescription());
        dto.setMainCategory(item.getMainCategory());
        dto.setSubCategory(item.getSubCategory());
        dto.setLanguage(item.getLanguage());
        dto.setLink(item.getLink());
        dto.setImage(assetService.toOut(item.getImage()));
        return dto;
    }
}
