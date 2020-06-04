package se.leafcoders.rosette.endpoint.group;

public class GroupData {

    public static Group admins() {
        Group group = new Group();
        group.setIdAlias("admins");
        group.setName("Admins");
        group.setDescription("All admin users");
        return group;
    }

    public static Group users() {
        Group group = new Group();
        group.setIdAlias("users");
        group.setName("Users");
        group.setDescription("Any user");
        return group;
    }

    public static GroupIn missingAllProperties() {
        return new GroupIn();
    }

    public static GroupIn invalidProperties() {
        GroupIn group = new GroupIn();
        group.setIdAlias("MustNotStartWithUpperCase");
        group.setName("");
        return group;
    }

    public static GroupIn newGroup() {
        return GroupData.newGroup("idGroup", "Group");
    }

    public static GroupIn newGroup(String idAlias, String name) {
        GroupIn group = new GroupIn();
        group.setIdAlias(idAlias);
        group.setName(name);
        return group;
    }
    
}
