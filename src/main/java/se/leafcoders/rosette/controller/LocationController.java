package se.ryttargardskyrkan.rosette.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import se.ryttargardskyrkan.rosette.model.Location;
import se.ryttargardskyrkan.rosette.service.LocationService;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
public class LocationController extends AbstractController {
	@Autowired
	private LocationService locationService;

	@RequestMapping(value = "locations/{id}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Location getLocation(@PathVariable String id) {
		return locationService.read(id);
	}

	@RequestMapping(value = "locations", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<Location> getLocations(HttpServletResponse response) {
		return locationService.readMany(new Query().with(new Sort(Sort.Direction.ASC, "name")));
	}

	@RequestMapping(value = "locations", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Location postLocation(@RequestBody Location location, HttpServletResponse response) {
		return locationService.create(location, response);
	}

	@RequestMapping(value = "locations/{id}", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	public void putLocation(@PathVariable String id, @RequestBody Location location, HttpServletResponse response) {
		locationService.update(id, location, response);
	}

	@RequestMapping(value = "locations/{id}", method = RequestMethod.DELETE, produces = "application/json")
	public void deleteLocation(@PathVariable String id, HttpServletResponse response) {
		locationService.delete(id, response);
	}
}
