package se.leafcoders.rosette.endpoint.assetfolder;

import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.hibernate.validator.constraints.Length;

import lombok.Data;
import se.leafcoders.rosette.core.exception.ApiString;
import se.leafcoders.rosette.core.validator.IdAlias;

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
