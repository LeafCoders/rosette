package se.leafcoders.rosette.controller.dto;

import org.hibernate.validator.constraints.Length;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import se.leafcoders.rosette.exception.ApiString;
import se.leafcoders.rosette.persistence.validator.IdAlias;

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
