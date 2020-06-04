package se.leafcoders.rosette.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.junit.Before;
import org.junit.Test;

import se.leafcoders.rosette.core.persistable.HtmlContent;
import se.leafcoders.rosette.endpoint.article.Article;
import se.leafcoders.rosette.endpoint.articleserie.ArticleSerie;
import se.leafcoders.rosette.endpoint.articletype.ArticleType;
import se.leafcoders.rosette.endpoint.articletype.ArticleType.RecordingStatus;
import se.leafcoders.rosette.endpoint.asset.Asset;
import se.leafcoders.rosette.endpoint.asset.AssetService;
import se.leafcoders.rosette.endpoint.podcast.Podcast;
import se.leafcoders.rosette.endpoint.resource.Resource;

public class PodcastFeedGeneratorTest {

    private AssetService assetService;
    private PodcastFeedGenerator generator;
    private Podcast podcast;
    private Article article;
    private Article article2;
    private HtmlContent articleContent;

    @Before
    public void before() {
        assetService = mock(AssetService.class);
        generator = new PodcastFeedGenerator(assetService);

        ArticleType articleType = mock(ArticleType.class);
        ArticleSerie articleSerie = mock(ArticleSerie.class);

        Asset image = mock(Asset.class);
        Asset recording = mock(Asset.class);

        Resource author = new Resource();
        author.setName("Author");

        articleContent = new HtmlContent();
        articleContent.setContentHtml("<p>ContentHtml</p>");
        articleContent.setContentRaw("ContentRaw");

        podcast = new Podcast();
        podcast.setArticleType(articleType);
        podcast.setIdAlias("AliasId");
        podcast.setTitle("Title");
        podcast.setSubTitle("SubTitle");
        podcast.setAuthorName("AuthorName");
        podcast.setAuthorEmail("author@email");
        podcast.setAuthorLink("https://my.com");
        podcast.setCopyright("Copyright");
        podcast.setDescription("Description");
        podcast.setMainCategory("MainCategory");
        podcast.setSubCategory("SubCategory");
        podcast.setLanguage("sv-SE");
        podcast.setArticlesLink("https://my.com/articles");
        podcast.setImage(image);
        podcast.setChangedDate(ClientServerTime.serverTimeNow());

        article = new Article();
        article.setId(1l);
        article.setArticleType(articleType);
        article.setArticleSerie(articleSerie);
        article.setEvent(null);
        article.setLastModifiedTime(ClientServerTime.serverTimeNow().minusMinutes(45));
        article.setTime(ClientServerTime.serverTimeNow().minusMinutes(30));
        article.setAuthors(Arrays.asList(author));
        article.setTitle("Title1");
        article.setContent(articleContent);
        article.setRecording(recording);
        article.setRecordingStatus(RecordingStatus.EXPECTING_RECORDING);

        article2 = new Article();
        article2.setId(1l);
        article2.setArticleType(articleType);
        article2.setArticleSerie(articleSerie);
        article2.setEvent(null);
        article2.setLastModifiedTime(ClientServerTime.serverTimeNow().minusMinutes(145));
        article2.setTime(ClientServerTime.serverTimeNow().minusMinutes(130));
        article2.setAuthors(Arrays.asList(author));
        article2.setTitle("Title2");
        article2.setContent(articleContent);
        article2.setRecording(recording);
        article2.setRecordingStatus(RecordingStatus.EXPECTING_RECORDING);

        when(articleSerie.getImage()).thenReturn(image);
        when(assetService.urlOfAsset(image)).thenReturn("https://my.com/image.png");
        when(assetService.urlOfAsset(null)).thenReturn(null);
    }

    @Test
    public void whenAllDataIsSpeicfied() throws JsonProcessingException, IOException {
        assertFalse(generator.getPodcastFeed(podcast, Arrays.asList(article)).isEmpty());
    }

    @Test
    public void whenSomePodcastDataIsNull() throws JsonProcessingException, IOException {
        podcast.setLanguage(null);
        assertFalse(generator.getPodcastFeed(podcast, Arrays.asList(article)).isEmpty());

        podcast.setArticleType(null);
        assertFalse(generator.getPodcastFeed(podcast, Arrays.asList(article)).isEmpty());

        podcast.setImage(null);
        assertFalse(generator.getPodcastFeed(podcast, Arrays.asList(article)).isEmpty());

        podcast.setTitle(null);
        assertFalse(generator.getPodcastFeed(podcast, Arrays.asList(article)).isEmpty());
    }

    @Test
    public void whenSomeArticleDataIsNull() throws JsonProcessingException, IOException {
        articleContent.setContentHtml(null);
        articleContent.setContentRaw(null);
        assertTrue(generator.getPodcastFeed(podcast, Arrays.asList(article)).contains("<item>"));

        article.setContent(null);
        assertTrue(generator.getPodcastFeed(podcast, Arrays.asList(article)).contains("<item>"));

        article.setAuthors(null);
        assertTrue(generator.getPodcastFeed(podcast, Arrays.asList(article)).contains("<item>"));

        article.setArticleSerie(null);
        assertTrue(generator.getPodcastFeed(podcast, Arrays.asList(article)).contains("<item>"));
        assertTrue(generator.getPodcastFeed(podcast, Arrays.asList(article)).contains("<item>"));
        assertTrue(generator.getPodcastFeed(podcast, Arrays.asList(article)).contains("<item>"));
        assertTrue(generator.getPodcastFeed(podcast, Arrays.asList(article)).contains("<item>"));
    }

    @Test
    public void skipArticleWhenNoRecording() throws JsonProcessingException, IOException {
        assertTrue(generator.getPodcastFeed(podcast, Arrays.asList(article)).contains("<item>"));

        article.setRecording(null);
        assertFalse(generator.getPodcastFeed(podcast, Arrays.asList(article)).contains("<item>"));
    }

    @Test
    public void skipArticleWhenNoTime() throws JsonProcessingException, IOException {
        final String result1 = generator.getPodcastFeed(podcast, Arrays.asList(article, article2));
        assertTrue(result1.contains(article.getTitle()));
        assertTrue(result1.contains(article2.getTitle()));

        article.setTime(ClientServerTime.serverTimeNow().plusMinutes(30));
        final String result2 = generator.getPodcastFeed(podcast, Arrays.asList(article, article2));
        assertFalse(result2.contains(article.getTitle()));
        assertTrue(result2.contains(article2.getTitle()));

        article2.setTime(ClientServerTime.serverTimeNow().plusMinutes(30));
        final String result3 = generator.getPodcastFeed(podcast, Arrays.asList(article, article2));
        assertFalse(result3.contains(article.getTitle()));
        assertFalse(result3.contains(article2.getTitle()));
    }

    @Test
    public void skipArticlesBeforeStartTime() throws JsonProcessingException, IOException {
        assertTrue(generator.getPodcastFeed(podcast, Arrays.asList(article)).contains("<item>"));

        article.setTime(ClientServerTime.serverTimeNow().plusMinutes(30));
        assertFalse(generator.getPodcastFeed(podcast, Arrays.asList(article)).contains("<item>"));
    }

    @Test
    public void sortArticlesWithLatestFirst() throws JsonProcessingException, IOException {
        article2.setTime(ClientServerTime.serverTimeNow().plusDays(1));
        String result = generator.getPodcastFeed(podcast, Arrays.asList(article, article2));
        assertTrue(result.indexOf("Title1") > result.indexOf("Title2"));

        article2.setTime(ClientServerTime.serverTimeNow().minusDays(1));
        result = generator.getPodcastFeed(podcast, Arrays.asList(article, article2));
        assertTrue(result.indexOf("Title1") < result.indexOf("Title2"));
    }

    @Test
    public void sortShallNotFailWhenNoTime() throws JsonProcessingException, IOException {
        article.setTime(null);
        article2.setTime(null);
        assertFalse(generator.getPodcastFeed(podcast, Arrays.asList(article)).isEmpty());
    }

}
