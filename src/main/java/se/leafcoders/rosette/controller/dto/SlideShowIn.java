package se.leafcoders.rosette.controller.dto;

import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import se.leafcoders.rosette.exception.ApiString;
import se.leafcoders.rosette.persistence.validator.IdAlias;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class SlideShowIn {

    @IdAlias
    private String idAlias;

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    private String name;

    @NotNull(message = ApiString.NOT_NULL)
    private Long assetFolderId;
}
