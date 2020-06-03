package se.leafcoders.rosette.persistence.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

import org.hibernate.validator.constraints.Length;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import se.leafcoders.rosette.exception.ApiString;
import se.leafcoders.rosette.persistence.validator.IdAlias;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "assetfolders")
public class AssetFolder extends Persistable {

    private static final long serialVersionUID = 2634706723961375417L;

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

    // Getters and setters

    public Boolean getStaticFileKey() {
        return staticFileKey != null ? staticFileKey : false;
    }
}
