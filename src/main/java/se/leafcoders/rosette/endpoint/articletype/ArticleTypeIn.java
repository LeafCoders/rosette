package se.leafcoders.rosette.endpoint.articletype;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.hibernate.validator.constraints.Length;

import lombok.Data;
import se.leafcoders.rosette.core.exception.ApiString;
import se.leafcoders.rosette.core.validator.IdAlias;
import se.leafcoders.rosette.core.validator.StringEnumeration;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArticleTypeIn {

    @IdAlias
    private String idAlias;

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    private String articlesTitle;

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    private String newArticleTitle;
    
    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    private String articleSeriesTitle;
    
    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    private String newArticleSerieTitle;

    @NotNull(message = ApiString.NOT_NULL)
    private Long imageFolderId;
    
    @NotNull(message = ApiString.NOT_NULL)
    private Long recordingFolderId;
    
    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    @StringEnumeration(enumClass = ArticleType.RecordingStatus.class)
    private String defaultRecordingStatus;

    @NotNull(message = ApiString.NOT_NULL)
    private Long authorResourceTypeId;
}
