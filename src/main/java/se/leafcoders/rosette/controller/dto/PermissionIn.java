package se.leafcoders.rosette.controller.dto;

import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import se.leafcoders.rosette.exception.ApiString;
import se.leafcoders.rosette.persistence.validator.ValidPermissions;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PermissionIn {

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    private String name;

    @NotNull(message = ApiString.NOT_NULL)
    @Range(min = 0, max = 3, message = ApiString.NUMBER_OUT_OF_RANGE)
    private Integer level;

    private Long entityId;

    @ValidPermissions
    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    private String patterns;

    // Getters and setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getPatterns() {
        return patterns;
    }

    public void setPatterns(String patterns) {
        this.patterns = patterns;
    }

}
