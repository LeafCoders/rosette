package se.leafcoders.rosette.endpoint.slideshow;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.hibernate.validator.constraints.Length;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import se.leafcoders.rosette.core.converter.RosetteDateTimeJsonDeserializer;
import se.leafcoders.rosette.core.converter.RosetteDateTimeJsonSerializer;
import se.leafcoders.rosette.core.exception.ApiString;
import se.leafcoders.rosette.core.persistable.Persistable;
import se.leafcoders.rosette.core.validator.DateTimeAfter;
import se.leafcoders.rosette.endpoint.asset.Asset;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "slides")
@DateTimeAfter(startDateTime = "startTime", endDateTime = "endTime", errorAt = "endTime")
public class Slide extends Persistable {

    private static final long serialVersionUID = -865616765574642271L;

    @JsonIgnore
    @NotNull(message = ApiString.NOT_NULL)
    @Column(name = "slideshow_id", nullable = false, insertable = false, updatable = false)
    protected Long slideShowId;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "slideshow_id", nullable = false)
    @NotNull(message = ApiString.NOT_NULL)
    protected SlideShow slideShow;

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    private String title;

    @JsonDeserialize(using = RosetteDateTimeJsonDeserializer.class)
    @JsonSerialize(using = RosetteDateTimeJsonSerializer.class)
    @NotNull(message = ApiString.NOT_NULL)
    private LocalDateTime startTime;

    @JsonDeserialize(using = RosetteDateTimeJsonDeserializer.class)
    @JsonSerialize(using = RosetteDateTimeJsonSerializer.class)
    private LocalDateTime endTime;

    // Number of seconds to display slide in slide show
    @NotNull(message = ApiString.NOT_NULL)
    @Min(value = 1, message = ApiString.DURATION_TOO_SHORT)
    private Integer duration = 10;

    @JsonIgnore
    @Column(name = "image_id", nullable = false, insertable = false, updatable = false)
    private Long imageId;

    @NotNull(message = ApiString.NOT_NULL)
    @ManyToOne
    @JoinColumn(name = "image_id")
    private Asset image;
    
    @NotNull(message = ApiString.NOT_NULL)
    private Long displayOrder;

    // Getters and setters

    public void setImage(Asset image) {
        this.image = image;
        this.imageId = image != null ? image.getId() : null;
    }
}
