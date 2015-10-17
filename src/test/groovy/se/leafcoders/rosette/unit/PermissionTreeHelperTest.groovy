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
    public void checkPermissionTest() throws JsonProcessingException, IOException {
        List<String> permissions = new ArrayList<String>()
        permissions.add("*:read:*")
        permissions.add("events:read:12")
        permissions.add("events:update,delete")
        permissions.add("events:update:*")
        permissions.add("events:create:12")
        permissions.add("events:create:*")

        PermissionTreeHelper permissionObject = new PermissionTreeHelper()
        permissionObject.create(permissions)

        assertTrue(PermissionTreeHelper.checkPermission(permissionObject.permissionTree, "events:read:19")) 
        assertTrue(PermissionTreeHelper.checkPermission(permissionObject.permissionTree, "events:update:19")) 
        assertTrue(PermissionTreeHelper.checkPermission(permissionObject.permissionTree, "events:delete")) 
        assertTrue(PermissionTreeHelper.checkPermission(permissionObject.permissionTree, "posters:read:19")) 

        assertFalse(PermissionTreeHelper.checkPermission(permissionObject.permissionTree, "posters:update:19")) 
        assertFalse(PermissionTreeHelper.checkPermission(permissionObject.permissionTree, "posters:update")) 
        assertFalse(PermissionTreeHelper.checkPermission(permissionObject.permissionTree, "events:assign")) 
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
