package se.leafcoders.rosette.endpoint.article;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.hibernate.validator.constraints.Length;

import lombok.Data;
import se.leafcoders.rosette.core.converter.RosetteDateTimeJsonDeserializer;
import se.leafcoders.rosette.core.converter.RosetteDateTimeJsonSerializer;
import se.leafcoders.rosette.core.exception.ApiString;
import se.leafcoders.rosette.core.validator.StringEnumeration;
import se.leafcoders.rosette.endpoint.articletype.ArticleType;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ArticleIn {

    @NotNull(message = ApiString.NOT_NULL)
    private Long articleTypeId;

    @NotNull(message = ApiString.NOT_NULL)
    private Long articleSerieId;

    private Long eventId;
    
    @NotNull(message = ApiString.NOT_NULL)
    @JsonDeserialize(using = RosetteDateTimeJsonDeserializer.class)
    @JsonSerialize(using = RosetteDateTimeJsonSerializer.class)
    private LocalDateTime time;

    private List<Long> authorIds;
    
    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    private String title;

    @Length(max = 10000, message = ApiString.STRING_MAX_10000_CHARS)
    private String contentRaw;

    @Length(max = 10000, message = ApiString.STRING_MAX_10000_CHARS)
    private String contentHtml;

    private Long recordingId;

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    @StringEnumeration(enumClass = ArticleType.RecordingStatus.class)
    private String recordingStatus;

    
    // Getters and setters

    public List<Long> getAuthorIds() {
        return Optional.ofNullable(authorIds).orElse(new ArrayList<>()).stream().filter(Objects::nonNull).collect(Collectors.toList());
    }
}
