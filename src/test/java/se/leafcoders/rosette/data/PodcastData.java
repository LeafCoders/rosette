package se.leafcoders.rosette.data;

import se.leafcoders.rosette.controller.dto.PodcastIn;

public class PodcastData {

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

}
