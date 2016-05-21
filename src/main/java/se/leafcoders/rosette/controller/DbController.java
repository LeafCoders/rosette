package se.leafcoders.rosette.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import se.leafcoders.rosette.security.PermissionAction;
import se.leafcoders.rosette.security.PermissionType;
import se.leafcoders.rosette.security.PermissionValue;
import se.leafcoders.rosette.service.SecurityService;

@RestController
public class DbController extends ApiV1Controller {
	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private SecurityService securityService;

	@RequestMapping(value = "db/rename/{collectionName}", method = RequestMethod.GET)
	public String renameCollection(@PathVariable String collectionName, @RequestParam(required = true) String newName) {
		checkPermission();
		mongoTemplate.getCollection(collectionName).rename(newName);
		return "Renamed collection '" + collectionName + "' to '" + newName + "'.\n";
	}

	@RequestMapping(value = "db/copy/{collectionName}", method = RequestMethod.GET)
	public String copyCollection(@PathVariable String collectionName, @RequestParam(required = true) String newName) {
		checkPermission();
		if (mongoTemplate.collectionExists(collectionName)) {
			mongoTemplate.getDb().doEval("db." + collectionName + ".copyTo('" + newName + "')");
			return "Copied collection '" + collectionName + "' to '" + newName + "'.\n";
		}
		return "Failed to copy collection '" + collectionName + "'. It doesn't exist.\n";
	}

    protected void checkPermission() {
    	securityService.checkPermission(new PermissionValue(PermissionType.ADMIN_DB, PermissionAction.UPDATE));
    }

}
