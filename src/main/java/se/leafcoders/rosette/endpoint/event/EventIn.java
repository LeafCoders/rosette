package se.leafcoders.rosette.endpoint.event;

import java.time.LocalDateTime;

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
import se.leafcoders.rosette.core.validator.DateTimeAfter;

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
