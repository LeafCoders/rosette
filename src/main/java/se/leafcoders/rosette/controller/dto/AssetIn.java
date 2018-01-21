package se.leafcoders.rosette.controller.dto;

import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import se.leafcoders.rosette.exception.ApiString;
import se.leafcoders.rosette.persistence.model.Asset;
import se.leafcoders.rosette.persistence.validator.StringEnumeration;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AssetIn {

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @StringEnumeration(enumClass = Asset.AssetType.class)
    private String type;

    @URL
    private String url;

    // Getters and setters

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}