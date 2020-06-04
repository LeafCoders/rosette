package se.leafcoders.rosette.endpoint.permissionset;

import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.hibernate.validator.constraints.Length;

import lombok.Data;
import se.leafcoders.rosette.core.exception.ApiString;
import se.leafcoders.rosette.core.validator.ValidPermissions;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class PermissionSetIn {

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    private String name;

    @ValidPermissions
    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    private String patterns;
}
