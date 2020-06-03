package se.leafcoders.rosette.data;

import se.leafcoders.rosette.controller.dto.PodcastIn;
import se.leafcoders.rosette.persistence.model.ArticleType;
import se.leafcoders.rosette.persistence.model.Asset;
import se.leafcoders.rosette.persistence.model.Podcast;

public class PodcastData {

    public static Podcast podcast1(ArticleType articleType, Asset podcastImage) {
        Podcast podcast = new Podcast();
        podcast.setArticleType(articleType);
        podcast.setIdAlias("podcast1");
        podcast.setTitle("Podcast Title 1");
        podcast.setSubTitle("Podcast SubTitle 1");
        podcast.setAuthorName("Podcast AuthorName 1");
        podcast.setAuthorEmail("podcast_1@domain");
        podcast.setAuthorLink("https://podcast_1.link");
        podcast.setCopyright("© 2020 Your name 1");
        podcast.setDescription("Podcast Description 1");
        podcast.setMainCategory("Religion & Spirituality");
        podcast.setSubCategory("Christianity");
        podcast.setLanguage("sv-SE");
        podcast.setArticlesLink("https://podcast_1.link/articles");
        podcast.setImage(podcastImage);
        return podcast;
    }

    public static Podcast podcast2(ArticleType articleType, Asset podcastImage) {
        Podcast podcast = new Podcast();
        podcast.setArticleType(articleType);
        podcast.setIdAlias("podcast2");
        podcast.setTitle("Podcast Title 2");
        podcast.setSubTitle("Podcast SubTitle 2");
        podcast.setAuthorName("Podcast AuthorName 2");
        podcast.setAuthorEmail("podcast_2@domain");
        podcast.setAuthorLink("https://podcast_2.link");
        podcast.setCopyright("© 2020 Your name 2");
        podcast.setDescription("Podcast Description 2");
        podcast.setMainCategory("Religion & Spirituality");
        podcast.setSubCategory("Christianity");
        podcast.setLanguage("sv-SE");
        podcast.setArticlesLink("https://podcast_2.link/articles");
        podcast.setImage(podcastImage);
        return podcast;
    }

    public static PodcastIn newPodcast(Long articleTypeId, Long podcastImageId) {
        PodcastIn podcast = new PodcastIn();
        podcast.setArticleTypeId(articleTypeId);
        podcast.setIdAlias("podcast");
        podcast.setTitle("Podcast Title");
        podcast.setSubTitle("Podcast SubTitle");
        podcast.setAuthorName("Podcast AuthorName");
        podcast.setAuthorEmail("podcast@domain");
        podcast.setAuthorLink("https://podcast.link");
        podcast.setCopyright("© 2020 Your name");
        podcast.setDescription("Podcast Description");
        podcast.setMainCategory("Religion & Spirituality");
        podcast.setSubCategory("Christianity");
        podcast.setLanguage("sv-SE");
        podcast.setArticlesLink("https://podcast.link/articles");
        podcast.setImageId(podcastImageId);
        return podcast;
    }

    public static PodcastIn missingAllProperties() {
        return new PodcastIn();
    }

    public static PodcastIn invalidProperties() {
        PodcastIn podcast = new PodcastIn();
        podcast.setArticleTypeId(-1L);
        podcast.setIdAlias("UppercaseNotOk");
        podcast.setTitle("Ok");
        podcast.setSubTitle("Ok");
        podcast.setAuthorName("Ok");
        podcast.setAuthorEmail("auther-at-mail.me");
        podcast.setAuthorLink("Ok");
        podcast.setCopyright("Ok");
        podcast.setDescription("Ok");
        podcast.setMainCategory("Ok");
        podcast.setSubCategory("Ok");
        podcast.setLanguage("Ok");
        podcast.setArticlesLink("Ok");
        podcast.setImageId(-1L);
        return podcast;
    }

}
