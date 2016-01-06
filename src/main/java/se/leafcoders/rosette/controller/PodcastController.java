package se.leafcoders.rosette.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import se.leafcoders.rosette.model.podcast.Podcast;
import se.leafcoders.rosette.service.PodcastService;
import se.leafcoders.rosette.util.ManyQuery;

@Controller
public class PodcastController extends AbstractController {
    @Autowired
    private PodcastService podcastService;

	@RequestMapping(value = "podcasts/{id}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Podcast getPodcast(@PathVariable String id) {
		return podcastService.read(id);
	}

	@RequestMapping(value = "podcasts", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<Podcast> getPodcasts(HttpServletRequest request) {
		return podcastService.readMany(new ManyQuery(request, "name"));
	}

	@RequestMapping(value = "podcasts", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Podcast postPodcast(@RequestBody Podcast educationType, HttpServletResponse response) {
		return podcastService.create(educationType, response);
	}

    @RequestMapping(value = "podcasts/{id}", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
    public void putPodcast(@PathVariable String id, HttpServletRequest request, HttpServletResponse response) {
		podcastService.update(id, request, response);
    }

	@RequestMapping(value = "podcasts/{id}", method = RequestMethod.DELETE, produces = "application/json")
	public void deletePodcast(@PathVariable String id, HttpServletResponse response) {
		podcastService.delete(id, response);
	}
}
