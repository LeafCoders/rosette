package se.leafcoders.rosette.endpoint.asset;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import lombok.Data;
import se.leafcoders.rosette.core.exception.ApiString;
import se.leafcoders.rosette.core.validator.StringEnumeration;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AssetIn {

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    @StringEnumeration(enumClass = Asset.AssetType.class)
    private String type;

    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    @URL
    private String url;

    @NotNull(message = ApiString.NOT_NULL)
    private Long folderId;
}
