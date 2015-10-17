package se.leafcoders.rosette.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import se.leafcoders.rosette.model.PermissionTreeNode;

/**
 * Permission tree builder for efficient permission checks.
 * A permission consists of levels separated by colon (:). Each level
 * can contain one or many values, each value separated by comma (,).
 * A value can be a * (any) or a text. Here are some examples:
 * 
 *   events:read
 *   events:read,update:12
 *   events:*:12
 *   *:read
 *
 */

public class PermissionTreeHelper {

    private static final String LEVEL_DIVIDER = ":";
    private static final String VALUE_DIVIDER = ",";
    private static final String ANY_VALUE = "*";
    
    private PermissionTreeNode permissionTree = new PermissionTreeNode();

    public void create(List<String> permissions) {
        permissions.forEach((String permission) -> {
            permission = trimEndOfPermissionString(permission);
            String[] levels = permission.split(LEVEL_DIVIDER);
            addPermissionLevel(levels, 0, permissionTree);
        });

        permissionTree = cleanUpPermissionTreeNode(permissionTree);
    }

    private String trimEndOfPermissionString(String permission) {
        while (permission.endsWith(ANY_VALUE) || permission.endsWith(LEVEL_DIVIDER)) {
            permission = permission.substring(0, permission.length() - 2);
        }
        return permission;
    }

    private void addPermissionLevel(String[] permissionLevels, final int levelIndex, PermissionTreeNode treeNode) {
        if (levelIndex < permissionLevels.length) {
            final boolean isLastLevel = levelIndex + 1 == permissionLevels.length;
            String permission = permissionLevels[levelIndex];

            ArrayList<String> permissionNames = new ArrayList<String>(Arrays.asList(permission.split(VALUE_DIVIDER)));
            permissionNames.forEach((String permissionName) -> {
                if (treeNode.containsKey(permissionName)) {
                    if (isLastLevel) {
                        PermissionTreeNode subTreeNode = new PermissionTreeNode();
                        subTreeNode.put(ANY_VALUE, new PermissionTreeNode());
                        treeNode.put(permissionName, subTreeNode);
                    } else {
                        addPermissionLevel(permissionLevels, levelIndex + 1, (PermissionTreeNode)treeNode.get(permissionName));
                    }
                } else {
                    PermissionTreeNode subTreeNode = new PermissionTreeNode();
                    if (isLastLevel) {
                        subTreeNode.put(ANY_VALUE, new PermissionTreeNode());
                    }
                    treeNode.put(permissionName, subTreeNode);
                    addPermissionLevel(permissionLevels, levelIndex + 1, subTreeNode);
                }
            });
        }
    }

    private PermissionTreeNode cleanUpPermissionTreeNode(PermissionTreeNode treeNode) {
        PermissionTreeNode allNode = (PermissionTreeNode) treeNode.get(ANY_VALUE);
        if (allNode != null && allNode.isEmpty()) {
            return null;
        } else {
            treeNode.forEach((String permissionName, Object subTreeNode) -> {
                treeNode.put(permissionName, cleanUpPermissionTreeNode((PermissionTreeNode) subTreeNode));
            });
            return treeNode;
        }
    }

    public PermissionTreeNode getTree() {
        return permissionTree;
    }

    public static boolean checkPermission(PermissionTreeNode treeRootNode, String permission) {
        if (treeRootNode != null) {
            String[] levels = permission.split(LEVEL_DIVIDER);
            return checkPermissionLevel(levels, 0, treeRootNode);
        } else {
            return false;
        }
    }

    private static boolean checkPermissionLevel(String[] permissionLevels, final int levelIndex, PermissionTreeNode treeNode) {
        if (treeNode.containsKey(ANY_VALUE)) {
            PermissionTreeNode allNode = (PermissionTreeNode) treeNode.get(ANY_VALUE);
            if (allNode == null) {
                return true;
            }
            if (checkPermissionLevel(permissionLevels, levelIndex + 1, allNode)) {
                return true;
            }
        }

        if (treeNode.containsKey(permissionLevels[levelIndex])) {
            PermissionTreeNode levelNode = (PermissionTreeNode) treeNode.get(permissionLevels[levelIndex]);
            if (levelNode == null) {
                return true;
            }
            if (checkPermissionLevel(permissionLevels, levelIndex + 1, levelNode)) {
                return true;
            }
        }
        
        return false;
    }
}
