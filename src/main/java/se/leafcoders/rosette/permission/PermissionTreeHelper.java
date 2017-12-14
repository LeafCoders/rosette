package se.leafcoders.rosette.permission;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Permission tree builder for efficient permission checks. A permission
 * consists of levels separated by colon (:). Each level can contain one or many
 * values, each value separated by slash (/). A value can be a * (any) or a
 * text.
 * 
 * Permissions stored in database are separated by comma (,).
 * 
 * Here are some examples:
 * 
 * events:read
 * events:read/update:12
 * events:*:12
 * *:read
 * *:view,*update
 *
 */

@SuppressWarnings("unchecked")
public class PermissionTreeHelper {

    public static final String PERMISSION_DIVIDER = ",";
    public static final String VALUE_DIVIDER = "/";
    private static final String LEVEL_DIVIDER = ":";
    private static final String ANY_VALUE = "*";

    private HashMap<String, Object> permissionTree = new HashMap<String, Object>();

    public void create(List<String> permissions) {
        permissions.forEach(
            (String permission) -> {
                permission = trimEndOfPermissionString(permission);
                String[] levels = permission.split(LEVEL_DIVIDER);
                addPermissionLevel(levels, 0, permissionTree);
            }
        );

        permissionTree = cleanUpPermissionTree(permissionTree);
    }

    private String trimEndOfPermissionString(String permission) {
        while (permission.length() > 1 && permission.endsWith(ANY_VALUE) || permission.endsWith(LEVEL_DIVIDER)) {
            permission = permission.substring(0, permission.length() - 2);
        }
        return permission;
    }

    private void addPermissionLevel(String[] permissionLevels, final int levelIndex, HashMap<String, Object> treeNode) {
        if (levelIndex < permissionLevels.length) {
            final boolean isLastLevel = levelIndex + 1 == permissionLevels.length;
            String permission = permissionLevels[levelIndex];

            ArrayList<String> permissionNames = new ArrayList<String>(Arrays.asList(permission.split(VALUE_DIVIDER)));
            permissionNames.forEach(
                (String permissionName) -> {
                    if (treeNode.containsKey(permissionName)) {
                        if (isLastLevel) {
                            HashMap<String, Object> subTreeNode = new HashMap<String, Object>();
                            subTreeNode.put(ANY_VALUE, new HashMap<String, Object>());
                            treeNode.put(permissionName, subTreeNode);
                        } else {
                            addPermissionLevel(permissionLevels, levelIndex + 1, (HashMap<String, Object>) treeNode.get(permissionName));
                        }
                    } else {
                        HashMap<String, Object> subTreeNode = new HashMap<String, Object>();
                        if (isLastLevel) {
                            subTreeNode.put(ANY_VALUE, new HashMap<String, Object>());
                        }
                        treeNode.put(permissionName, subTreeNode);
                        addPermissionLevel(permissionLevels, levelIndex + 1, subTreeNode);
                    }
                }
            );
        }
    }

    private HashMap<String, Object> cleanUpPermissionTree(HashMap<String, Object> treeNode) {
        HashMap<String, Object> allNode = (HashMap<String, Object>) treeNode.get(ANY_VALUE);
        if (allNode != null && allNode.isEmpty()) {
            return null;
        } else {
            treeNode.forEach(
                (String permissionName, Object subTreeNode) -> {
                    treeNode.put(permissionName, cleanUpPermissionTree((HashMap<String, Object>) subTreeNode));
                }
            );
            return treeNode;
        }
    }

    public HashMap<String, Object> getTree() {
        return permissionTree;
    }

    public static boolean checkPermission(HashMap<String, Object> treeRootNode, String permission) {
        if (treeRootNode != null) {
            String[] levels = permission.split(LEVEL_DIVIDER);
            return checkPermissionLevel(levels, 0, treeRootNode);
        } else {
            return false;
        }
    }

    private static boolean checkPermissionLevel(String[] permissionLevels, final int levelIndex, HashMap<String, Object> treeNode) {
        if (permissionLevels.length == levelIndex) {
            return false;
        }
        if (treeNode.containsKey(ANY_VALUE)) {
            HashMap<String, Object> allNode = (HashMap<String, Object>) treeNode.get(ANY_VALUE);
            if (allNode == null) {
                return true;
            }
            if (checkPermissionLevel(permissionLevels, levelIndex + 1, allNode)) {
                return true;
            }
        }

        if (treeNode.containsKey(permissionLevels[levelIndex])) {
            HashMap<String, Object> levelNode = (HashMap<String, Object>) treeNode.get(permissionLevels[levelIndex]);
            if (levelNode == null) {
                return true;
            }
            if (checkPermissionLevel(permissionLevels, levelIndex + 1, levelNode)) {
                return true;
            }
        }

        return false;
    }

    public static boolean hasValidPermissionFormat(final String permission) {
        if (permission != null) {
            boolean lastCharIsDivider = false;
            for (int i = 0; i < permission.length(); ++i) {
                boolean currentCharIsDivider = permission.charAt(i) == LEVEL_DIVIDER.charAt(0) || permission.charAt(i) == VALUE_DIVIDER.charAt(0);
                boolean currentCharIsAny = permission.charAt(i) == ANY_VALUE.charAt(0);

                if (permission.charAt(i) == ' ') {
                    return false;
                }
                if (i == 0) {
                    if (currentCharIsDivider) {
                        return false;
                    }
                } else if (currentCharIsDivider) {
                    if (lastCharIsDivider) {
                        return false;
                    }
                } else if (currentCharIsAny) {
                    if (!lastCharIsDivider) {
                        return false;
                    }
                }

                lastCharIsDivider = currentCharIsDivider;
            }

            if (lastCharIsDivider) {
                return false;
            }
            return true;
        } else {
            return false;
        }
    }
}
