package se.ryttargardskyrkan.rosette.controller;

import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class AuthorizationController extends AbstractController {
    @RequestMapping(value = "authorizations", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Map<String, Boolean> getAuthorizations(@RequestParam String permissions) {
        Map<String, Boolean> authorizations = new HashMap<String, Boolean>();

        if (permissions != null) {
            String[] permissionList = permissions.split(",");
            boolean[] permittedList = SecurityUtils.getSubject().isPermitted(permissionList);

            for (int i = 0; i < permissionList.length; i++) {
                if (permissionList[i] != null) {
                    String permission = permissionList[i].trim();
                    boolean permitted = permittedList[i];

                    authorizations.put(permission, permitted);
                }
            }
        }

        return authorizations;
    }
}
