package se.leafcoders.rosette.unit

import static org.junit.Assert.*

import java.io.IOException
import java.util.ArrayList
import java.util.List

import org.junit.Test

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper

import se.leafcoders.rosette.integration.util.TestUtil
import se.leafcoders.rosette.security.PermissionTreeHelper


public class PermissionTreeHelperTest {

    ObjectMapper mapper = new ObjectMapper()

    @Test
    public void checkPermissionFormat() throws JsonProcessingException, IOException {
		assertTrue(PermissionTreeHelper.hasValidPermissionFormat("events"))
		assertTrue(PermissionTreeHelper.hasValidPermissionFormat("events:*"))
		assertTrue(PermissionTreeHelper.hasValidPermissionFormat("events:*:resourceTypes"))
		assertTrue(PermissionTreeHelper.hasValidPermissionFormat("*:update:*"))

		assertFalse(PermissionTreeHelper.hasValidPermissionFormat("**"))
		assertFalse(PermissionTreeHelper.hasValidPermissionFormat("events:"))
		assertFalse(PermissionTreeHelper.hasValidPermissionFormat("events::"))
		assertFalse(PermissionTreeHelper.hasValidPermissionFormat(":events"))
		assertFalse(PermissionTreeHelper.hasValidPermissionFormat("events*"))
		assertFalse(PermissionTreeHelper.hasValidPermissionFormat("events: read:*"))
    }

    @Test
    public void createPermissionDbObjectTest() throws JsonProcessingException, IOException {
        List<String> permissions = new ArrayList<String>()
        permissions.add("*:read:*")
        permissions.add("events:read:12")
        permissions.add("events:update,delete")
        permissions.add("events:update:*")
        permissions.add("events:create:12")
        permissions.add("events:create:*")
        
        PermissionTreeHelper permissionObject = new PermissionTreeHelper()
        permissionObject.create(permissions)
        
        String expectedData = """
            {
                "*": { "read": null },
                "events": { "read": { "12": null }, "update": null, "delete": null, "create": null }
            }
        """
        TestUtil.assertJsonEquals(expectedData, toJSON(permissionObject.getTree())) 
    }

    @Test
    public void checkPermittedPermissions() throws JsonProcessingException, IOException {
		isPermitted("bookings:read:19", when("*:read"))
		isPermitted("bookings:read:19", when("*:read:*"))
		isPermitted("bookings:read:19", when("bookings:read"))
		isPermitted("bookings:read:19", when("*:update", "bookings:read"))

		isPermitted("events:update:resourceTypes:posters", when("events:update"))
		isPermitted("events:update:resourceTypes:posters", when("events:update:resourceTypes"))
		isPermitted("events:update:resourceTypes:posters", when("events:update:resourceTypes:posters"))
		isPermitted("events:update:resourceTypes:posters", when("events:update:resourceTypes:posters:*"))
		isPermitted("events:update:resourceTypes:posters", when("events:*:*:*"))

		isPermitted("events:update:12", when("*:read", "*:delete:12", "events:*:19", "events:delete", "events:read:12", "events:update:*"))
	}    

    @Test
    public void checkDeniedPermissions() throws JsonProcessingException, IOException {
		isNotPermitted("bookings:read", when("bookings:read:12"))
		isNotPermitted("bookings:read", when("bookings:read:12", "*:update", "posters:read"))
		isNotPermitted("bookings:read", when("bookings:*:13"))
    }

	private void isPermitted(String permission, HashMap<String, Object> permissionTree) {
		assertTrue(PermissionTreeHelper.checkPermission(permissionTree, permission))
	}
	
	private void isNotPermitted(String permission, HashMap<String, Object> permissionTree) {
		assertFalse(PermissionTreeHelper.checkPermission(permissionTree, permission))
	}
	
	private HashMap<String, Object> when(String... permissions) {
		PermissionTreeHelper permissionObject = new PermissionTreeHelper()
		permissionObject.create(Arrays.asList(permissions))
		return permissionObject.permissionTree
	}
	
    private String toJSON(Object data) {
        try {
            return mapper.writeValueAsString(data)
        } catch (JsonProcessingException e) {
            e.printStackTrace()
            return ""
        }
    }

}