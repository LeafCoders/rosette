package se.leafcoders.rosette.endpoint.podcast;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;
import static se.leafcoders.rosette.test.matcher.Matchers.isIdOf;
import static se.leafcoders.rosette.test.matcher.Matchers.isValidationError;

import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import se.leafcoders.rosette.core.exception.ApiString;
import se.leafcoders.rosette.endpoint.AbstractControllerTest;
import se.leafcoders.rosette.endpoint.CommonRequestTests;
import se.leafcoders.rosette.endpoint.article.ArticleData;
import se.leafcoders.rosette.endpoint.article.ArticleRepository;
import se.leafcoders.rosette.endpoint.articleserie.ArticleSerie;
import se.leafcoders.rosette.endpoint.articleserie.ArticleSerieData;
import se.leafcoders.rosette.endpoint.articleserie.ArticleSerieRepository;
import se.leafcoders.rosette.endpoint.articletype.ArticleType;
import se.leafcoders.rosette.endpoint.articletype.ArticleTypeData;
import se.leafcoders.rosette.endpoint.articletype.ArticleTypeRepository;
import se.leafcoders.rosette.endpoint.asset.Asset;
import se.leafcoders.rosette.endpoint.assetfolder.AssetFolder;
import se.leafcoders.rosette.endpoint.assetfolder.AssetFolderData;
import se.leafcoders.rosette.endpoint.resourcetype.ResourceType;
import se.leafcoders.rosette.endpoint.resourcetype.ResourceTypeData;
import se.leafcoders.rosette.endpoint.resourcetype.ResourceTypeRepository;
import se.leafcoders.rosette.util.ClientServerTime;

public class PodcastControllerTest extends AbstractControllerTest {

        @Autowired
        private PodcastRepository podcastRepository;

        @Autowired
        private ResourceTypeRepository resourceTypeRepository;

        @Autowired
        private ArticleTypeRepository articleTypeRepository;

        @Autowired
        private ArticleSerieRepository articleSerieRepository;

        @Autowired
        private ArticleRepository articleRepository;

        private final CommonRequestTests crt = new CommonRequestTests(this, Podcast.class);
        private Asset podcastImage;
        private ArticleType articleType;

        @Before
        public void setup() throws Exception {
                super.setup();

                final AssetFolder assetFolder = givenAssetFolder(AssetFolderData.image());
                podcastImage = givenAssetInFolder(assetFolder.getId(), "image.png", "image.png", "image/png");
                final ResourceType resourceType = resourceTypeRepository.save(ResourceTypeData.preacher());
                articleType = articleTypeRepository
                                .save(ArticleTypeData.existingArticleType("ArticleType", resourceType, assetFolder,
                                                assetFolder));
        }

        @Test
        public void getPodcast() throws Exception {
                user1 = givenUser(user1);
                final Podcast podcast = podcastRepository.save(PodcastData.podcast1(articleType, podcastImage));

                crt.allGetOneTests(user1, "podcasts:read", "/api/podcasts", podcast.getId())
                                .andExpect(jsonPath("$.idAlias", is(podcast.getIdAlias())))
                                .andExpect(jsonPath("$.title", is(podcast.getTitle())))
                                .andExpect(jsonPath("$.description", is(podcast.getDescription())));
        }

        @Test
        public void getPodcasts() throws Exception {
                user1 = givenUser(user1);
                final Podcast podcast1 = podcastRepository.save(PodcastData.podcast1(articleType, podcastImage));
                final Podcast podcast2 = podcastRepository.save(PodcastData.podcast2(articleType, podcastImage));

                crt.allGetManyTests(user1, "podcasts:read", "/api/podcasts")
                                .andExpect(jsonPath("$", hasSize(2)))
                                .andExpect(jsonPath("$[0].id", isIdOf(podcast1)))
                                .andExpect(jsonPath("$[1].id", isIdOf(podcast2)));
        }

        @Test
        public void createPodcast() throws Exception {
                user1 = givenUser(user1);
                final PodcastIn podcast = PodcastData.newPodcast(articleType.getId(), podcastImage.getId());
                final String requiredPermissions = "podcasts:create,articleTypes:read:" + articleType.getId()
                                + ",assets:read:"
                                + podcastImage.getId();

                crt.allPostTests(user1, requiredPermissions, "/api/podcasts", json(podcast))
                                .andExpect(jsonPath("$.idAlias", is(podcast.getIdAlias())))
                                .andExpect(jsonPath("$.title", is(podcast.getTitle())))
                                .andExpect(jsonPath("$.description", is(podcast.getDescription())));

                // Check missing properties
                crt.postExpectBadRequest(user1, "/api/podcasts", json(PodcastData.missingAllProperties()))
                                .andExpect(jsonPath("$", hasSize(14)))
                                .andExpect(jsonPath("$[0]", isValidationError("articleTypeId", ApiString.NOT_NULL)))
                                .andExpect(jsonPath("$[1]",
                                                isValidationError("articlesLink", ApiString.STRING_NOT_EMPTY)))
                                .andExpect(jsonPath("$[2]",
                                                isValidationError("authorEmail", ApiString.STRING_NOT_EMPTY)))
                                .andExpect(jsonPath("$[3]",
                                                isValidationError("authorLink", ApiString.STRING_NOT_EMPTY)))
                                .andExpect(jsonPath("$[4]",
                                                isValidationError("authorName", ApiString.STRING_NOT_EMPTY)))
                                .andExpect(jsonPath("$[5]", isValidationError("copyright", ApiString.STRING_NOT_EMPTY)))
                                .andExpect(jsonPath("$[6]",
                                                isValidationError("description", ApiString.STRING_NOT_EMPTY)))
                                .andExpect(jsonPath("$[7]", isValidationError("idAlias", ApiString.STRING_NOT_EMPTY)))
                                .andExpect(jsonPath("$[8]", isValidationError("imageId", ApiString.NOT_NULL)))
                                .andExpect(jsonPath("$[9]", isValidationError("language", ApiString.STRING_NOT_EMPTY)))
                                .andExpect(jsonPath("$[10]",
                                                isValidationError("mainCategory", ApiString.STRING_NOT_EMPTY)))
                                .andExpect(jsonPath("$[11]",
                                                isValidationError("subCategory", ApiString.STRING_NOT_EMPTY)))
                                .andExpect(jsonPath("$[12]", isValidationError("subTitle", ApiString.STRING_NOT_EMPTY)))
                                .andExpect(jsonPath("$[13]", isValidationError("title", ApiString.STRING_NOT_EMPTY)));

                // Check invalid properties
                crt.postExpectBadRequest(user1, "/api/podcasts", json(PodcastData.invalidProperties()))
                                .andExpect(jsonPath("$", hasSize(2)))
                                .andExpect(jsonPath("$[0]", isValidationError("authorEmail", ApiString.EMAIL_INVALID)))
                                .andExpect(jsonPath("$[1]",
                                                isValidationError("idAlias", ApiString.IDALIAS_INVALID_FORMAT)));
        }

        @Test
        public void updatePodcast() throws Exception {
                user1 = givenUser(user1);
                final Podcast podcast = podcastRepository.save(PodcastData.podcast1(articleType, podcastImage));

                String jsonData = mapToJson(data -> data.put("title", "Super podcast"));

                crt.allPutTests(user1, "podcasts:update", "/api/podcasts", podcast.getId(), jsonData)
                                .andExpect(jsonPath("$.idAlias", is(podcast.getIdAlias())))
                                .andExpect(jsonPath("$.title", is("Super podcast")))
                                .andExpect(jsonPath("$.description", is(podcast.getDescription())));
        }

        @Test
        public void deletePodcast() throws Exception {
                user1 = givenUser(user1);
                final Podcast podcast = podcastRepository.save(PodcastData.podcast1(articleType, podcastImage));

                crt.allDeleteTests(user1, "podcasts:delete", "/api/podcasts", podcast.getId());
        }

        @Test
        public void getPodcastFeed() throws Exception {
                user1 = givenUser(user1);

                final LocalDateTime pastTime = ClientServerTime.serverTimeNow().minusMinutes(5);
                final LocalDateTime futureTime = ClientServerTime.serverTimeNow().plusMinutes(5);

                final Podcast podcast = podcastRepository.save(PodcastData.podcast1(articleType, podcastImage));
                final ArticleSerie articleSerie = articleSerieRepository
                                .save(ArticleSerieData.existingArticleSerie(articleType, "idAlias", podcastImage));
                articleRepository.save(
                                ArticleData.existingArticle(articleType, articleSerie, pastTime, "PastTitle", null,
                                                podcastImage));
                articleRepository.save(
                                ArticleData.existingArticle(articleType, articleSerie, futureTime, "FutureTitle", null,
                                                podcastImage));

                givenPermissionForUser(user1, "podcasts:public");

                crt.getManySuccessRssXml(user1, "/api/podcasts/feed/" + podcast.getIdAlias())
                                .andExpect(xpath("/rss/channel/title").string(podcast.getTitle()))
                                .andExpect(xpath("/rss/channel/item").nodeCount(1))
                                .andExpect(xpath("/rss/channel/item[1]/title").string("PastTitle"));
        }

}
