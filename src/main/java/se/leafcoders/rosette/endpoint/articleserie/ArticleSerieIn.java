package se.leafcoders.rosette.endpoint.articleserie;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.hibernate.validator.constraints.Length;

import lombok.Data;
import se.leafcoders.rosette.core.exception.ApiString;
import se.leafcoders.rosette.core.validator.IdAlias;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ArticleSerieIn {

    @NotNull(message = ApiString.NOT_NULL)
    private Long articleTypeId;
    
    @IdAlias
    private String idAlias;

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    private String title;

    @Length(max = 10000, message = ApiString.STRING_MAX_10000_CHARS)
    private String contentRaw;

    @Length(max = 10000, message = ApiString.STRING_MAX_10000_CHARS)
    private String contentHtml;

    private Long imageId;
}
