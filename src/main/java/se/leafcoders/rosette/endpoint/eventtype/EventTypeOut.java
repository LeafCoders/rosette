package se.leafcoders.rosette.endpoint.eventtype;

import java.util.List;

import lombok.Data;
import se.leafcoders.rosette.endpoint.resourcetype.ResourceTypeRefOut;

@Data
public class EventTypeOut {

    private Long id;
    private String idAlias;
    private String name;
    private String description;
    private List<ResourceTypeRefOut> resourceTypes;
    private Boolean isPublic;
}
