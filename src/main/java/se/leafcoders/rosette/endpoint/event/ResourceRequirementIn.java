package se.leafcoders.rosette.endpoint.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ResourceRequirementIn {

    private Long resourceTypeId;
}
