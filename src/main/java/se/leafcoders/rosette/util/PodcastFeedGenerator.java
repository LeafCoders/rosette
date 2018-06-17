package se.leafcoders.rosette.util;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.leafcoders.rosette.persistence.model.Article;
import se.leafcoders.rosette.persistence.model.ArticleSerie;
import se.leafcoders.rosette.persistence.model.Asset;
import se.leafcoders.rosette.persistence.model.Podcast;
import se.leafcoders.rosette.persistence.service.AssetService;

// https://help.apple.com/itc/podcasts_connect/
// http://podcasts.apple.com/resources/spec/ApplePodcastsSpecUpdatesiOS11.pdf
@Service
public class PodcastFeedGenerator {
    
    @Autowired
    private AssetService assetService;

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
        articles.sort((a, b) -> b.getTime().compareTo(a.getTime()));
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
        return String.join("\n", new String[] {
                tag("title", noAmp(podcast.getTitle())),
                tag("itunes:subtitle", noAmp(podcast.getSubTitle())),
    
                tag("description", toContentData(podcast.getDescription())),
//                tag("itunes:summary", toContentData(podcast.getDescription())),
    
                tag("itunes:author", noAmp(podcast.getAuthorName())),
                tag("link", podcast.getLink()),
                tag("language", podcast.getLanguage()),
                tag("copyright", noAmp(podcast.getCopyright())),
                tag("itunes:explicit", "clean"),
                tag("generator", "LeafCoders/Rosette"),
    
                tag("itunes:image", "", "href=\"" + assetService.urlOfAsset(podcast.getImage()) + "\""),
                "<itunes:category text=\"" + noAmp(podcast.getMainCategory()) + "\">",
                    "<itunes:category text=\"" + noAmp(podcast.getSubCategory()) + "\"/>",
                "</itunes:category>",
        });
    }

    private String getItemData(Article article) {
        final ArticleSerie articleSerie = article.getArticleSerie();
        final Asset recording = article.getRecording();
        final ZonedDateTime pubDate = ZonedDateTime.of(article.getTime(), ZoneId.systemDefault());
        
        return String.join("\n", new String[] {
                tag("title", noAmp(article.getTitle())),
                tag("itunes:subtitle", noAmp(article.getArticleSerie().getTitle())),
                tag("description", toContentData(article.getContent().getContentPodcast())),
                //tag("itunes:summary", toContentData(article.getContent())),
                //tag("content:encoded", toHtmlContentData(article.getContent())), // May contain <p>, <ol>, <ul> or <a>
                //tag("link", article.getLinkToWebPageWithArticle()),

                tag("itunes:author", article.getAuthors().stream().map(a -> noAmp(a.getName())).collect(Collectors.joining(" ,"))),
                tag("itunes:explicit", "clean"), tag("guid", article.getId().toString()),
                tag("pubDate", pubDate.format(DateTimeFormatter.RFC_1123_DATE_TIME)),

                tag("itunes:image", "", "href=\"" + assetService.urlOfAsset(articleSerie.getImage()) + "\""),

                tag("enclosure", "",
                        "url=\"" + assetService.urlOfAsset(recording) + "\"",
                        "length=\"" + recording.getFileSize() + "\"",
                        "type=\"" + recording.getMimeType() + "\""),
                tag("itunes:duration", toDuration(recording.getDuration()))
        });
    }

    private String tag(String tagName, String tagValue, String... tagElements) {
        String elements = tagElements.length > 0 ? " " + String.join(" ", tagElements) : "";
        return "<" + tagName + elements + ">" + tagValue + "</" + tagName + ">";
    }

    private String noAmp(String text) {
        return text != null ? text.replace("&", "&amp;") : "";
    }

    private String toContentData(String text) {
        if (text == null) {
            return "";
        }

        boolean isHtmlContent = text.startsWith("<");
        if (isHtmlContent) {
            return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("’", "&apos;")
                    .replace("\"", "&quot;");
        } else {
            return "<![CDATA[" + text + "]]>";
        }
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

}
