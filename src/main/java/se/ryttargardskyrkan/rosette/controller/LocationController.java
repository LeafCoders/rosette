package se.ryttargardskyrkan.rosette.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import se.ryttargardskyrkan.rosette.exception.NotFoundException;
import se.ryttargardskyrkan.rosette.model.Location;
import se.ryttargardskyrkan.rosette.security.MongoRealm;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Controller
public class LocationController extends AbstractController {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private MongoRealm mongoRealm;

    @RequestMapping(value = "locations/{id}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Location getLocation(@PathVariable String id) {
        checkPermission("read:locations:" + id);

        Location location = mongoTemplate.findById(id, Location.class);
        if (location == null) {
            throw new NotFoundException();
        }
        return location;
    }

    @RequestMapping(value = "locations", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public List<Location> getLocations(HttpServletResponse response) {
        Query query = new Query();
        query.with(new Sort(new Sort.Order(Sort.Direction.ASC, "name")));

        List<Location> locationsInDatabase = mongoTemplate.find(query, Location.class);
        List<Location> locations = new ArrayList<Location>();
        if (locationsInDatabase != null) {
            for (Location locationInDatabase : locationsInDatabase) {
                if (isPermitted("read:locations:" + locationInDatabase.getId())) {
                    locations.add(locationInDatabase);
                }
            }
        }
        return locations;
    }

    @RequestMapping(value = "locations", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public Location postLocation(@RequestBody Location location, HttpServletResponse response) {
        checkPermission("create:locations");
        validate(location);

        mongoTemplate.insert(location);

        response.setStatus(HttpStatus.CREATED.value());
        return location;
    }

    @RequestMapping(value = "locations/{id}", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
    public void putLocation(@PathVariable String id, @RequestBody Location location, HttpServletResponse response) {
        checkPermission("update:locations:" + id);
        validate(location);

        Update update = new Update();
        update.set("name", location.getName());
        update.set("description", location.getDescription());

        if (mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(id)), update, Location.class).getN() == 0) {
            throw new NotFoundException();
        }

        response.setStatus(HttpStatus.OK.value());
    }

    @RequestMapping(value = "locations/{id}", method = RequestMethod.DELETE, produces = "application/json")
    public void deleteLocation(@PathVariable String id, HttpServletResponse response) {
        checkPermission("delete:locations:" + id);

        Location deletedLocation = mongoTemplate.findAndRemove(Query.query(Criteria.where("id").is(id)), Location.class);
        if (deletedLocation == null) {
            throw new NotFoundException();
        } else {
            response.setStatus(HttpStatus.OK.value());
        }
    }
}
