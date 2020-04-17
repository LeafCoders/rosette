package se.leafcoders.rosette.controller.dto;

import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import se.leafcoders.rosette.exception.ApiString;
import se.leafcoders.rosette.persistence.validator.ValidPermissions;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
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
}
