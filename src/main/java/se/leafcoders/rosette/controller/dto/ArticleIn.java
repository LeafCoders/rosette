package se.leafcoders.rosette.controller.dto;

import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import se.leafcoders.rosette.exception.ApiString;
import se.leafcoders.rosette.persistence.converter.RosetteDateTimeJsonDeserializer;
import se.leafcoders.rosette.persistence.converter.RosetteDateTimeJsonSerializer;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ArticleIn {

    @NotNull(message = ApiString.NOT_NULL)
    private Long articleTypeId;

    @NotNull(message = ApiString.NOT_NULL)
    private Long articleSerieId;
    
    @NotNull(message = ApiString.NOT_NULL)
    @JsonDeserialize(using = RosetteDateTimeJsonDeserializer.class)
    @JsonSerialize(using = RosetteDateTimeJsonSerializer.class)
    private LocalDateTime time;

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    private String title;

    @Length(max = 10000, message = ApiString.STRING_MAX_10000_CHARS)
    private String content;

    // Getters and setters

    public Long getArticleTypeId() {
        return articleTypeId;
    }

    public void setArticleTypeId(Long articleTypeId) {
        this.articleTypeId = articleTypeId;
    }

    public Long getArticleSerieId() {
        return articleSerieId;
    }

    public void setArticleSerieId(Long articleSerieId) {
        this.articleSerieId = articleSerieId;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
