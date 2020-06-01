package se.leafcoders.rosette.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;
import se.leafcoders.rosette.controller.dto.ArticlePublicOut;
import se.leafcoders.rosette.persistence.converter.ClientServerTime;
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
        final LocalDateTime now = ClientServerTime.serverTimeNow();
        articles.stream().filter(a -> a.getTime() != null && a.getTime().isBefore(now))
                .sorted((a, b) -> b.getTime() != null ? b.getTime().compareTo(a.getTime()) : -1)
                .forEach(article -> {
                    if (article.getRecording() != null && article.getTime() != null) {
                        podcastData.add("<item>");
                        podcastData.add(getItemData(podcast, article));
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
                // TAGS FROM: RSS 2.0 Specification
                // The name of the channel. It's how people refer to your service.
                tag("title").cdataContent(podcast.getTitle()),
                // Phrase or sentence describing the channel. Supports CDATA for rich HTML.
                tag("description").cdataContent(podcast.getDescription()),
                // The URL to the HTML website corresponding to the channel.
                tag("link").rawContent(podcast.getAuthorLink()),
                // A language ISO 639 code, e.g. "en-us".
                tag("language").rawContent(podcast.getLanguage()),
                // A string
                tag("copyright").plainContent(podcast.getCopyright()),
                // A string indicating the program used to generate the channel.
                tag("generator").rawContent("LeafCoders/Rosette"),
                // Specifies a GIF, JPEG or PNG image that can be displayed with the channel.
                tag("image")
                        .tagContent(tag("link").rawContent(podcast.getAuthorLink()))
                        .tagContent(tag("title").plainContent(podcast.getTitle()))
                        .tagContent(tag("url").rawContent(assetService.urlOfAsset(podcast.getImage()))),

                // TAGS FROM: Apple Podcaster
                // https://help.apple.com/itc/podcasts_connect/#/itcb54353390
                tag("itunes:subtitle").plainContent(podcast.getSubTitle()),
                // Phrase or sentence describing the channel. Max 4000 characters. Supports
                // CDATA for rich HTML.
                tag("itunes:summary").cdataContent(podcast.getDescription()),
                // Author of the channel. String.
                tag("itunes:author").plainContent(podcast.getAuthorName()),
                // Owner of the channel
                tag("itunes:owner")
                        .tagContent(tag("itunes:name").plainContent(podcast.getAuthorName()))
                        .tagContent(tag("itunes:email").plainContent(podcast.getAuthorEmail())),
                // The podcast parental advisory information. "yes" or "no"
                tag("itunes:explicit").rawContent("no"),
                // The type of show. "episodic" or "serial".
                tag("itunes:type").rawContent("episodic"),
                // Minimum size of 1400 x 1400 pixels
                tag("itunes:image").attribute("href", assetService.urlOfAsset(podcast.getImage())),
                // Main category and sub category from a specified list.
                tag("itunes:category").attribute("text", podcast.getMainCategory())
                        .tagContent(tag("itunes:category").attribute("text", podcast.getSubCategory()))
        };
        return Stream.of(tags).map(Tag::toString).collect(Collectors.joining("\n"));
    }

    private String getItemData(final Podcast podcast, final Article article) {
        final ArticleSerie articleSerie = Optional.ofNullable(article.getArticleSerie()).orElse(new ArticleSerie());
        final Asset recording = Optional.ofNullable(article.getRecording()).orElse(new Asset());
        final ZonedDateTime pubDate = ZonedDateTime.of(article.getTime(), ZoneId.systemDefault());
        final String author = article.getAuthors().stream().map(a -> a.getName()).collect(Collectors.joining(" ,"));
        final String articleLink = podcast.getArticlesLink() != null
                ? podcast.getArticlesLink() + "/" + ArticlePublicOut.slug(article)
                : null;

        List<String> info = new ArrayList<>();
        Optional.ofNullable(author).filter(t -> !t.isEmpty()).ifPresent(info::add);
        Optional.ofNullable(articleSerie.getTitle()).ifPresent(info::add);
        Optional.ofNullable(articleLink).filter(t -> !t.isEmpty()).ifPresent(info::add);

        String content = Optional.ofNullable(article.getContent().getContentHtml()).orElse("");
        if (!info.isEmpty()) {
            content = "<p>" + String.join("<br>", info) + "</p>" + content;
        }
        if (content.length() > 3950) {
            content = content.substring(0, 3950) + "...";
        }

        Tag[] tags = new Tag[] {
                // TAGS FROM: RSS 2.0 Specification
                // The title of the item.
                tag("title").cdataContent(article.getTitle()),
                // The item synopsis.
                tag("description").cdataContent(content),
                // The URL of the item.
                tag("link").rawContent(articleLink),
                // A string that uniquely identifies the item. Must add `isPermaLink="false"` if
                // the GUID is not an URL
                tag("guid").attribute("isPermaLink", "false")
                        .rawContent(podcast.getIdAlias() + "-" + article.getId().toString()),
                // Indicates when the item was published. RFC 2822.
                tag("pubDate").rawContent(pubDate.format(DateTimeFormatter.RFC_1123_DATE_TIME)),
                // Describes a media object that is attached to the item.
                tag("enclosure")
                        .attribute("url", assetService.urlOfAsset(recording))
                        .attribute("length", "" + recording.getFileSize())
                        .attribute("type", recording.getMimeType()),

                // TAGS FROM: Apple Podcaster
                // https://help.apple.com/itc/podcasts_connect/#/itcb54353390
                tag("itunes:subtitle").cdataContent(articleSerie.getTitle()),
                // The item synopsis.
                tag("itunes:summary").cdataContent(content),
                tag("itunes:author").plainContent(author),
                // The episode parental advisory information. "yes" or "no"
                tag("itunes:explicit").rawContent("no"),
                // Minimum size of 1400 x 1400 pixels
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

        Tag cdataContent(String htmlContent) {
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
