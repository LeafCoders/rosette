package se.leafcoders.rosette.controller.publicdata;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import se.leafcoders.rosette.comparator.PosterComparator;
import se.leafcoders.rosette.model.Poster;
import se.leafcoders.rosette.service.PosterService;

@Controller
public class PublicPosterController extends PublicDataController {

	@Autowired
	private PosterService posterService;

	@RequestMapping(value = "posters", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<Poster> getPosters() {
		checkPermission();

		List<Poster> posters = posterService.readMany(new Query(activePostersCriteria()), false);
        Collections.sort(posters, new PosterComparator());
		return posters;
	}	

	private Criteria activePostersCriteria() {
		final Calendar now = Calendar.getInstance();
		return Criteria.where("endTime").gt(now.getTime()).and("startTime").lt(now.getTime());
	}
}
