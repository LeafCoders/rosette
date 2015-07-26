package se.leafcoders.rosette.service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import se.leafcoders.rosette.model.event.Event;

@Service
public class PublicEventService {
	@Autowired
	protected MongoTemplate mongoTemplate;

	public List<Event> calendarEventsBetween(Date from, Date to) {
		Query query = new Query();
		if (from != null && to != null) {
			query.addCriteria(Criteria.where("startTime").gte(from).lt(to));
		}

		return readMany(query.with(new Sort(Sort.Direction.ASC, "startTime")));
	}

	public List<Event> readManyForEventTypes(Date from, Date to, String[] eventTypes) {
		Query query = new Query();
		if (from != null && to != null) {
			query.addCriteria(Criteria.where("startTime").gte(from).lt(to));
		}
		if (eventTypes != null) {
			query.addCriteria(Criteria.where("eventType.id").in(Arrays.asList(eventTypes)));
		}

		return readMany(query.with(new Sort(Sort.Direction.ASC, "startTime")));
	}

	private List<Event> readMany(Query query) {
		query.addCriteria(Criteria.where("isPublic").is(Boolean.TRUE));
		return mongoTemplate.find(query, Event.class);
	}
}
