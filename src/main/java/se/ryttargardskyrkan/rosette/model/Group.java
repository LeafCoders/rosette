package se.ryttargardskyrkan.rosette.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "groups")
public class Group extends TypeBasedModel {
}
