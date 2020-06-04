package se.leafcoders.rosette.endpoint.resourcetype;

import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.hibernate.validator.constraints.Length;

import lombok.Data;
import se.leafcoders.rosette.core.exception.ApiString;
import se.leafcoders.rosette.core.validator.IdAlias;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ResourceTypeIn {

    @IdAlias
    private String idAlias;

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    private String name;

    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS) // "error.description.max200Chars")
    private String description;
}
