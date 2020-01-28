package se.leafcoders.rosette.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import com.fasterxml.jackson.core.JsonProcessingException;
import se.leafcoders.rosette.persistence.model.Article;
import se.leafcoders.rosette.persistence.model.ArticleSerie;
import se.leafcoders.rosette.persistence.model.ArticleType;
import se.leafcoders.rosette.persistence.model.ArticleType.RecordingStatus;
import se.leafcoders.rosette.persistence.model.Asset;
import se.leafcoders.rosette.persistence.model.HtmlContent;
import se.leafcoders.rosette.persistence.model.Podcast;
import se.leafcoders.rosette.persistence.model.Resource;
import se.leafcoders.rosette.persistence.service.AssetService;

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
        podcast.setChangedDate(LocalDateTime.now());

        article = new Article();
        article.setId(1l);
        article.setArticleType(articleType);
        article.setArticleSerie(articleSerie);
        article.setEvent(null);
        article.setLastModifiedTime(LocalDateTime.now());
        article.setTime(LocalDateTime.now());
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
        article2.setLastModifiedTime(LocalDateTime.now());
        article2.setTime(LocalDateTime.now());
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
    public void skippArticleWhenNoRecording() throws JsonProcessingException, IOException {
        assertTrue(generator.getPodcastFeed(podcast, Arrays.asList(article)).contains("<item>"));

        article.setRecording(null);
        assertFalse(generator.getPodcastFeed(podcast, Arrays.asList(article)).contains("<item>"));
    }
    
    @Test
    public void skippArticleWhenNoTime() throws JsonProcessingException, IOException {
        assertTrue(generator.getPodcastFeed(podcast, Arrays.asList(article)).contains("<item>"));
        
        article.setTime(null);
        assertFalse(generator.getPodcastFeed(podcast, Arrays.asList(article)).contains("<item>"));
    }
    
    @Test
    public void sortArticlesWithLatestFirst() throws JsonProcessingException, IOException {
        article2.setTime(LocalDateTime.now().plusDays(1));
        String result = generator.getPodcastFeed(podcast, Arrays.asList(article, article2));
        assertTrue(result.indexOf("Title1") > result.indexOf("Title2"));

        article2.setTime(LocalDateTime.now().minusDays(1));
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
