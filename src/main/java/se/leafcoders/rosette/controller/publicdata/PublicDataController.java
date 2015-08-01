package se.leafcoders.rosette.controller.publicdata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import se.leafcoders.rosette.controller.AbstractController;
import se.leafcoders.rosette.security.PermissionAction;
import se.leafcoders.rosette.security.PermissionType;
import se.leafcoders.rosette.security.PermissionValue;
import se.leafcoders.rosette.service.SecurityService;

@RequestMapping("v1/public")
@CrossOrigin
public class PublicDataController extends AbstractController {

	@Autowired
	protected SecurityService security;

	protected void checkPermission() {
		security.checkPermission(new PermissionValue(PermissionType.PUBLIC_DATA, PermissionAction.READ));
	}
}
