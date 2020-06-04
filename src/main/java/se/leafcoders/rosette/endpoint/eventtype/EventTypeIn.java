package se.leafcoders.rosette.endpoint.eventtype;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.hibernate.validator.constraints.Length;

import lombok.Data;
import se.leafcoders.rosette.core.exception.ApiString;
import se.leafcoders.rosette.core.validator.IdAlias;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class EventTypeIn {

    @IdAlias
    private String idAlias;

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    private String name;

    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    private String description;

    @NotNull(message = ApiString.NOT_NULL)
    private Boolean isPublic;

    public Boolean getIsPublic() {
        return isPublic != null ? isPublic : true;
    }
}
