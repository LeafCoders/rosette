package se.leafcoders.rosette.controller.dto;

import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.URL;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import se.leafcoders.rosette.exception.ApiString;
import se.leafcoders.rosette.persistence.model.Asset;
import se.leafcoders.rosette.persistence.validator.StringEnumeration;

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
