package se.leafcoders.rosette.controller.dto;

import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import se.leafcoders.rosette.exception.ApiString;
import se.leafcoders.rosette.persistence.validator.IdAlias;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SlideShowIn {

    @IdAlias
    private String idAlias;

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    private String name;

    @NotNull(message = ApiString.NOT_NULL)
    private Long assetFolderId;
    
    // Getters and setters

    public String getIdAlias() {
        return idAlias;
    }

    public void setIdAlias(String idAlias) {
        this.idAlias = idAlias;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getAssetFolderId() {
        return assetFolderId;
    }

    public void setAssetFolderId(Long assetFolderId) {
        this.assetFolderId = assetFolderId;
    }
}
