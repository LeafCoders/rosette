package se.leafcoders.rosette.model;

import java.util.HashMap;

import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.databind.JsonNode;

@Document(collection = "permissionTrees")
public class PermissionTree extends IdBasedModel {

    private HashMap<String, Object> tree;

    @Override
    public void update(JsonNode rawData, BaseModel updateFrom) {
    }

    // Getters and setters

    public HashMap<String, Object> getTree() {
        return tree;
    }

    public void setTree(HashMap<String, Object> tree) {
        this.tree = tree;
    }
}
