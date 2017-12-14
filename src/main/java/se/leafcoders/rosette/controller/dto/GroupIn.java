package se.leafcoders.rosette.controller.dto;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import se.leafcoders.rosette.exception.ApiString;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupIn {

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Pattern(regexp = "[a-z][a-zA-Z0-9]+", message = ApiString.IDALIAS_INVALID_FORMAT)
    private String idAlias;

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    private String name;

    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    private String description;

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
}
