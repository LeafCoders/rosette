package se.leafcoders.rosette.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import se.leafcoders.rosette.persistence.model.Article;
import se.leafcoders.rosette.persistence.model.ArticleSerie;
import se.leafcoders.rosette.persistence.model.Asset;
import se.leafcoders.rosette.persistence.model.Podcast;

public class PodcastFeedGenerator {

    public static String getPodcastFeed(Podcast podcast, List<Article> articles) {

        List<String> podcastData = new ArrayList<String>();

        // XML header and <rss>
        podcastData.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        podcastData.add("<rss xmlns:itunes=\"http://www.itunes.com/dtds/podcast-1.0.dtd\" version=\"2.0\" xml:lang='"
                + podcast.getLanguage() + "'>");

        // Start <channel>
        podcastData.add("<channel>");
        podcastData.add(getChannelData(podcast));

        // Add all education items
/*        
        articles.forEach(article -> {
            if (article.getRecording() != null && article.getTime() != null) {
                podcastData.add("<item>");
                podcastData.add(getItemData(article));
                podcastData.add("</item>");
            }
        });
*/
        // End <channel>
        podcastData.add("</channel>");

        // End <rss>
        podcastData.add("</rss>");

        return String.join("\n", podcastData);
    }

    private static String getChannelData(Podcast podcast) {
        return String.join("\n", new String[] {
                tag("title", noAmp(podcast.getTitle())),
                tag("itunes:subtitle", noAmp(podcast.getSubTitle())),
    
                tag("description", toContentData(podcast.getDescription())),
                tag("itunes:summary", toContentData(podcast.getDescription())),
    
                tag("itunes:author", noAmp(podcast.getAuthorName())), tag("link", podcast.getLink()),
                tag("language", podcast.getLanguage()), tag("copyright", noAmp(podcast.getCopyright())),
                tag("itunes:explicit", "clean"), tag("generator", "LeafCoders/Rosette"),
    
                tag("itunes:image", "", "href=\"" + podcast.getImage().getUrl() + "\""),
                "<itunes:category text=\"" + noAmp(podcast.getMainCategory()) + "\">",
                "<itunes:category text=\"" + noAmp(podcast.getSubCategory()) + "\"/>", "</itunes:category>",
        });
    }

    private static String getItemData(Article article) {
        ArticleSerie articleSerie = article.getArticleSerie();
        Asset recording = null; //article.getRecording();
        return String.join("\n", new String[] {
                tag("title", noAmp(article.getTitle())),
                tag("description", toContentData(article.getContent())),
                tag("itunes:summary", toContentData(article.getContent())),

                tag("itunes:author", article.getAuthors().stream().map(a -> noAmp(a.getFullName())).collect(Collectors.joining(" ,"))),
                tag("itunes:explicit", "clean"), tag("guid", article.getId().toString()),
                tag("pubDate", new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.ENGLISH).format(article.getTime())),

                // TODO: Is it possible to add a image for each episode?
                // tag("itunes:image", "", "href=\"" + articleSerie.getImage().getFileUrl() + "\""),

                tag("enclosure", "",
                        "url=\"" + recording.getUrl() + "\"",
                        "length=\"" + recording.getFileSize() + "\"",
                        "type=\"" + recording.getMimeType() + "\""),
                tag("itunes:duration", toDuration(recording.getDuration()))
        });
    }

    private static String tag(String tagName, String tagValue, String... tagElements) {
        String elements = tagElements.length > 0 ? " " + String.join(" ", tagElements) : "";
        return "<" + tagName + elements + ">" + tagValue + "</" + tagName + ">";
    }

    private static String noAmp(String text) {
        return text != null ? text.replace("&", "&amp;") : "";
    }

    private static String toContentData(String text) {
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

    private static String toDuration(Long totalSeconds) {
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
