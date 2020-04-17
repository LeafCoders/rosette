package se.leafcoders.rosette.controller.dto;

import org.hibernate.validator.constraints.Length;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import se.leafcoders.rosette.exception.ApiString;
import se.leafcoders.rosette.persistence.validator.IdAlias;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class AssetFolderIn {

    @IdAlias
    private String idAlias;

    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    private String name;

    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    private String description;

    private String allowedMimeTypes;

    private Boolean staticFileKey;
}
