package se.leafcoders.rosette.endpoint.slideshow;

import java.time.LocalDateTime;

import javax.validation.constraints.Min;
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
public class SlideIn {

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    private String title;

    @JsonDeserialize(using = RosetteDateTimeJsonDeserializer.class)
    @JsonSerialize(using = RosetteDateTimeJsonSerializer.class)
    private LocalDateTime startTime;

    @JsonDeserialize(using = RosetteDateTimeJsonDeserializer.class)
    @JsonSerialize(using = RosetteDateTimeJsonSerializer.class)
    private LocalDateTime endTime;

    @Min(value = 1, message = ApiString.DURATION_TOO_SHORT)
    private Integer duration;

    @NotNull(message = ApiString.NOT_NULL)
    private Long imageId;
}
