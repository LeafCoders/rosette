package se.leafcoders.rosette.controller.dto;

import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;
import se.leafcoders.rosette.persistence.converter.RosetteDateTimeJsonDeserializer;
import se.leafcoders.rosette.persistence.converter.RosetteDateTimeJsonSerializer;

@Data
public class ArticleOut {

    private Long id;
    private Long articleTypeId;
    private String articleTypeIdAlias;
    private ArticleSerieRefOut articleSerie;
    private EventRefOut event;

    @JsonDeserialize(using = RosetteDateTimeJsonDeserializer.class)
    @JsonSerialize(using = RosetteDateTimeJsonSerializer.class)
    private LocalDateTime time;

    private List<ResourceRefOut> authors;
    private String title;
    private String contentRaw;
    private String contentHtml;
    private AssetOut recording;
    private String recordingStatus;
}
