package se.leafcoders.rosette.persistence.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotEmpty;
import se.leafcoders.rosette.exception.ApiString;
import se.leafcoders.rosette.persistence.validator.IdAlias;

@Entity
@Table(name = "assetfolders")
public class AssetFolder extends Persistable {

    @IdAlias
    @Column(nullable = false, unique = true)
    private String idAlias;

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    @Column(nullable = false, unique = true)
    private String name;

    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    private String description;

    private String allowedMimeTypes;
    
    private Boolean staticFileKey = false;


    public AssetFolder() {}


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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAllowedMimeTypes() {
        return allowedMimeTypes;
    }

    public void setAllowedMimeTypes(String allowedMimeTypes) {
        this.allowedMimeTypes = allowedMimeTypes;
    }

    public Boolean getStaticFileKey() {
        return staticFileKey;
    }

    public void setStaticFileKey(Boolean staticFileKey) {
        this.staticFileKey = staticFileKey;
    }

}
