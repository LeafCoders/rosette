package se.leafcoders.rosette.controller.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import se.leafcoders.rosette.exception.ApiString;
import se.leafcoders.rosette.persistence.converter.RosetteDateTimeJsonDeserializer;
import se.leafcoders.rosette.persistence.converter.RosetteDateTimeJsonSerializer;
import se.leafcoders.rosette.persistence.validator.DateTimeAfter;

@DateTimeAfter(startDateTime = "startTime", endDateTime = "endTime", errorAt = "endTime")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class EventIn {

    @NotNull(message = ApiString.NOT_NULL)
    @JsonDeserialize(using = RosetteDateTimeJsonDeserializer.class)
    @JsonSerialize(using = RosetteDateTimeJsonSerializer.class)
    private LocalDateTime startTime;

    @JsonDeserialize(using = RosetteDateTimeJsonDeserializer.class)
    @JsonSerialize(using = RosetteDateTimeJsonSerializer.class)
    private LocalDateTime endTime;

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    private String title;

    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    private String description;

    @Length(max = 4000, message = ApiString.STRING_MAX_4000_CHARS)
    private String privateDescription;
    
    @NotNull(message = ApiString.NOT_NULL)
    private Long eventTypeId;
    
    @NotNull(message = ApiString.NOT_NULL)
    private Boolean isPublic;

    public Boolean getIsPublic() {
        return isPublic != null ? isPublic : true;
    }
}
