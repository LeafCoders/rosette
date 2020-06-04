package se.leafcoders.rosette.endpoint.permission;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import lombok.Data;
import se.leafcoders.rosette.core.exception.ApiString;
import se.leafcoders.rosette.core.validator.ValidPermissions;

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
