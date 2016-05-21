package se.leafcoders.rosette.controller.publicdata;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import se.leafcoders.rosette.exception.NotFoundException;
import se.leafcoders.rosette.model.education.Education;
import se.leafcoders.rosette.model.education.EducationTheme;
import se.leafcoders.rosette.model.podcast.Podcast;
import se.leafcoders.rosette.service.PublicEducationThemeService;
import se.leafcoders.rosette.util.QueryId;

@RestController
public class PodcastFeedController extends PublicDataController {
    @Autowired
    protected MongoTemplate mongoTemplate;
    @Autowired
    private PublicEducationThemeService publicEducationThemeService;

	@RequestMapping(value = "podcasts/{id}", method = RequestMethod.GET, produces = "application/rss+xml; charset=UTF-8")
	public String getPodcast(@PathVariable String id, HttpServletResponse response) {
	    checkPermission();

	    Podcast podcast = mongoTemplate.findById(id, Podcast.class);
        if (podcast == null) {
            throw new NotFoundException("Podcast", id);
        }

        Query query = new Query(Criteria.where("educationType.id").is(QueryId.get(podcast.getEducationType().getId())));
        List<Education> educations = mongoTemplate.find(query, Education.class);
        
        List<String> podcastData = new ArrayList<String>();

        // XML header and <rss>
        podcastData.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        podcastData.add("<rss xmlns:itunes=\"http://www.itunes.com/dtds/podcast-1.0.dtd\" version=\"2.0\" xml:lang='" + podcast.getLanguage() + "'>");
        
        // Start <channel>
        podcastData.add("<channel>");
        podcastData.add(getChannelData(podcast));
        
        // Add all education items
        educations.forEach((Education education) -> {
            if (education.getRecording() != null && education.getTime() != null) {
                podcastData.add("<item>"); 
                podcastData.add(getItemData(education));
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
            tag("itunes:summary", toContentData(podcast.getDescription())),

            tag("itunes:author", noAmp(podcast.getAuthorName())),
            tag("link", podcast.getLink()),
            tag("language", podcast.getLanguage()),
            tag("copyright", noAmp(podcast.getCopyright())),
            tag("itunes:explicit", "clean"),
            tag("generator", "LeafCoders/Rosette"),
            
            tag("itunes:image", "",
                    "href=\"" + podcast.getImage().getFileUrl() + "\""
            ),
            "<itunes:category text=\"" + noAmp(podcast.getMainCategory()) + "\">",
            "<itunes:category text=\"" + noAmp(podcast.getSubCategory()) + "\"/>",
            "</itunes:category>",
        });
    }
    
	private String getItemData(Education education) {
        EducationTheme educationTheme = publicEducationThemeService.read(education.getEducationTheme().getId());
        
        return String.join("\n", new String[] {
            tag("title", noAmp(education.getTitle())),

            tag("description", toContentData(education.getContent())),
            tag("itunes:summary", toContentData(education.getContent())),

            tag("itunes:author", noAmp(education.getAuthorName())),
            tag("itunes:explicit", "clean"),
            tag("guid", education.getId()),
            tag("pubDate", new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.ENGLISH).format(education.getTime())),

            tag("itunes:image", "",
                    "href=\"" + educationTheme.getImage().getFileUrl() + "\""
            ),
            tag("enclosure", "",
                    "url=\"" +  education.getRecording().getFileUrl() + "\"",
                    "length=\"" + education.getRecording().getFileSize() + "\"",
                    "type=\"" + education.getRecording().getMimeType() + "\""
            ),
            tag("itunes:duration", toDuration(education.getRecording().getDuration()))
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
	        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("’", "&apos;").replace("\"", "&quot;");
	    } else {
            return "<![CDATA[" + text + "]]>";
	    }
	}
	
	private String toDuration(Long totalSeconds) {
        if (totalSeconds != null) {
            Long hours = totalSeconds/3600; 
            Long minutes = (totalSeconds - 3600*hours)/60; 
            Long seconds = totalSeconds - 3600*hours - 60*minutes; 
            return hours + (minutes > 9 ? ":" : ":0") + minutes + ":" + (seconds > 9 ? "" : "0") + seconds;   
        } else {
            return "";
        }
	}
}
