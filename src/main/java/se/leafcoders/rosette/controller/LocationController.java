package se.leafcoders.rosette.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import se.leafcoders.rosette.model.Location;
import se.leafcoders.rosette.service.LocationService;
import se.leafcoders.rosette.util.ManyQuery;

@RestController
public class LocationController extends ApiV1Controller {
	@Autowired
	private LocationService locationService;

	@RequestMapping(value = "locations/{id}", method = RequestMethod.GET, produces = "application/json")
	public Location getLocation(@PathVariable String id) {
		return locationService.read(id);
	}

	@RequestMapping(value = "locations", method = RequestMethod.GET, produces = "application/json")
	public List<Location> getLocations(HttpServletRequest request) {
        return locationService.readMany(new ManyQuery(request, "name"));
	}

	@RequestMapping(value = "locations", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public Location postLocation(@RequestBody Location location, HttpServletResponse response) {
		return locationService.create(location, response);
	}

	@RequestMapping(value = "locations/{id}", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	public void putLocation(@PathVariable String id, HttpServletRequest request, HttpServletResponse response) {
		locationService.update(id, request, response);
	}

	@RequestMapping(value = "locations/{id}", method = RequestMethod.DELETE, produces = "application/json")
	public void deleteLocation(@PathVariable String id, HttpServletResponse response) {
		locationService.delete(id, response);
	}
}
