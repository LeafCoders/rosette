package se.leafcoders.rosette.controller.dto;

import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;
import se.leafcoders.rosette.persistence.converter.RosetteDateTimeJsonSerializer;

@Data
public class ResourceOut {

    private Long id;
    private String name;
    private String description;
    private List<ResourceTypeRefOut> resourceTypes;
    private UserRefOut user;
    @JsonSerialize(using = RosetteDateTimeJsonSerializer.class)
    private LocalDateTime lastUseTime;
}
