package se.leafcoders.rosette.model;

import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.databind.JsonNode;

@Document(collection = "permissionTrees")
public class PermissionTree extends IdBasedModel {

    private PermissionTreeNode tree;

    @Override
    public void update(JsonNode rawData, BaseModel updateFrom) {
    }

    // Getters and setters

    public PermissionTreeNode getTree() {
        return tree;
    }

    public void setTree(PermissionTreeNode tree) {
        this.tree = tree;
    }
}
