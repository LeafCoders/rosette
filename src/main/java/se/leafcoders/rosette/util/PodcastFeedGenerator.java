package se.leafcoders.rosette.util;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;
import se.leafcoders.rosette.persistence.model.Article;
import se.leafcoders.rosette.persistence.model.ArticleSerie;
import se.leafcoders.rosette.persistence.model.Asset;
import se.leafcoders.rosette.persistence.model.Podcast;
import se.leafcoders.rosette.persistence.service.AssetService;

// https://cyber.harvard.edu/rss/rss.html
// https://help.apple.com/itc/podcasts_connect/
// https://developers.google.com/search/reference/podcast/rss-feed

@Service
public class PodcastFeedGenerator {
    
    private final AssetService assetService;
    
    public PodcastFeedGenerator(AssetService assetService) {
        this.assetService = assetService;
    }

    public String getPodcastFeed(Podcast podcast, List<Article> articles) {
        List<String> podcastData = new ArrayList<String>();

        // XML header and <rss>
        podcastData.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        podcastData.add("<rss xmlns:itunes=\"http://www.itunes.com/dtds/podcast-1.0.dtd\" version=\"2.0\" xml:lang=\""
                + podcast.getLanguage() + "\">");

        // Start <channel>
        podcastData.add("<channel>");
        podcastData.add(getChannelData(podcast));

        // Add all education items - Should be item with latest time first
        articles.sort((a, b) -> b.getTime() != null ? b.getTime().compareTo(a.getTime()) : -1);
        articles.forEach(article -> {
            if (article.getRecording() != null && article.getTime() != null) {
                podcastData.add("<item>");
                podcastData.add(getItemData(article));
                podcastData.add("</item>");
            }
        });

        // End <channel>
        podcastData.add("</channel>");

        // End <rss>
        podcastData.add("</rss>");

        return String.join("\n", podcastData);
    }

    private String getChannelData(Podcast podcast) {
        Tag[] tags = new Tag[] {
            // RSS 2.0 Specification
            tag("title").plainContent(podcast.getTitle()),
            tag("description").htmlContent(podcast.getDescription()),
            tag("link").rawContent(podcast.getLink()),
            tag("language").rawContent(podcast.getLanguage()),
            tag("copyright").plainContent(podcast.getCopyright()),
            tag("generator").rawContent("LeafCoders/Rosette"),
            tag("image")
                .tagContent(tag("link").rawContent(podcast.getLink()))
                .tagContent(tag("title").plainContent(podcast.getTitle()))
                .tagContent(tag("url").rawContent(assetService.urlOfAsset(podcast.getImage()))),

            // Apple Podcaster https://help.apple.com/itc/podcasts_connect/#/itcb54353390
            tag("itunes:subtitle").plainContent(podcast.getSubTitle()),
            tag("itunes:summary").htmlContent(podcast.getDescription()),
            tag("itunes:author").plainContent(podcast.getAuthorName()),
            tag("itunes:explicit").rawContent("false"),
            tag("itunes:type").rawContent("episodic"),
            tag("itunes:image").attribute("href", assetService.urlOfAsset(podcast.getImage())),
            
            tag("itunes:category").attribute("text", podcast.getMainCategory())
                .tagContent(tag("itunes:category").attribute("text", podcast.getSubCategory()))
        };
        return Stream.of(tags).map(Tag::toString).collect(Collectors.joining("\n"));
    }

    private String getItemData(Article article) {
        final ArticleSerie articleSerie = Optional.ofNullable(article.getArticleSerie()).orElse(new ArticleSerie());
        final Asset recording = Optional.ofNullable(article.getRecording()).orElse(new Asset());
        final ZonedDateTime pubDate = ZonedDateTime.of(article.getTime(), ZoneId.systemDefault());
        
        Tag[] tags = new Tag[] {
            // RSS 2.0 Specification
            tag("title").plainContent(article.getTitle()),
            tag("description").htmlContent(article.getContent().getContentRaw()),
            //tag("link", article.getLinkToWebPageWithArticle()),
            tag("author").plainContent(article.getAuthors().stream().map(a -> a.getName()).collect(Collectors.joining(" ,"))),
            tag("guid").rawContent(article.getId().toString()),
            tag("pubDate").rawContent(pubDate.format(DateTimeFormatter.RFC_1123_DATE_TIME)),
            tag("enclosure")
                .attribute("url", assetService.urlOfAsset(recording))
                .attribute("length", "" + recording.getFileSize())
                .attribute("type", recording.getMimeType()),

            // Apple Podcaster https://help.apple.com/itc/podcasts_connect/#/itcb54353390
            tag("itunes:subtitle").plainContent(articleSerie.getTitle()),
            tag("itunes:summary").htmlContent(article.getContent().getContentHtml()),
            tag("content:encoded").htmlContent(article.getContent().getContentHtml()),
            tag("itunes:author").plainContent(article.getAuthors().stream().map(a -> a.getName()).collect(Collectors.joining(" ,"))),
            tag("itunes:explicit").rawContent("false"),
            tag("itunes:image").attribute("href", assetService.urlOfAsset(articleSerie.getImage())),
            tag("itunes:duration").rawContent(toDuration(recording.getDuration()))
        };
        return Stream.of(tags).map(Tag::toString).collect(Collectors.joining("\n"));
    }
    
    private Tag tag(String tagName) {
        return new Tag(tagName);
    }

    private String toDuration(Long totalSeconds) {
        if (totalSeconds != null) {
            Long hours = totalSeconds / 3600;
            Long minutes = (totalSeconds - 3600 * hours) / 60;
            Long seconds = totalSeconds - 3600 * hours - 60 * minutes;
            return hours + (minutes > 9 ? ":" : ":0") + minutes + ":" + (seconds > 9 ? "" : "0") + seconds;
        } else {
            return "";
        }
    }

    private static class Tag {
        private final String tagName;
        private List<String> attributes = new ArrayList<>();
        private String content = "";
        
        Tag(String tagName) {
            this.tagName = tagName;
        }

        Tag attribute(String name, String value) {
            attributes.add(name + "=\"" + HtmlSanitize.sanitize(value) + "\"");
            return this;
        }

        Tag rawContent(String rawContent) {
            Optional.ofNullable(rawContent).ifPresent(c -> content += c);
            return this;
        }

        Tag plainContent(String plainContent) {
            Optional.ofNullable(HtmlSanitize.sanitize(plainContent)).ifPresent(c -> content += c);
            return this;
        }

        Tag htmlContent(String htmlContent) {
            Optional.ofNullable(toCDATA(toAllowedPodcastTags(htmlContent))).ifPresent(c -> content += c);
            return this;
        }
        
        Tag tagContent(Tag tagContent) {
            Optional.ofNullable(tagContent).ifPresent(c -> content += c);
            return this;
        }
        
        @Override
        public String toString() {
            String attrs = attributes.isEmpty() ? "" : " " + String.join(" ", attributes);
            return "<" + tagName + attrs + ">" + content + "</" + tagName + ">";
        }

        private String toCDATA(String htmlContent) {
            return htmlContent != null ? "<![CDATA[" + htmlContent + "]]>" : "";
        }

        // Allowed tags are <p>, <ol>, <ul>, <li> and <a>
        private String toAllowedPodcastTags(String htmlContent) {
            if (htmlContent == null) {
                return "";
            }
            return htmlContent
                    .replace("</p>", "</p><p></p>")
                    .replace("<h1>", "<p></p><p>=== ")
                    .replace("</h1>", " ===</p><p></p>")
                    .replace("<h2>", "<p></p><p>--- ")
                    .replace("</h2>", " ---</p><p></p>")
                    .replace("<blockquote>", "<p>--------</p><p>&nbsp;")
                    .replace("</blockquote>", "</p><p>--------</p>");
            // TODO: Convert "<img>" to "<a>"
        }
    }
}
