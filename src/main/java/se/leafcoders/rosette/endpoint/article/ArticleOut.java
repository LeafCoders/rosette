package se.leafcoders.rosette.endpoint.article;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;
import se.leafcoders.rosette.core.converter.RosetteDateTimeJsonDeserializer;
import se.leafcoders.rosette.core.converter.RosetteDateTimeJsonSerializer;
import se.leafcoders.rosette.endpoint.articleserie.ArticleSerieRefOut;
import se.leafcoders.rosette.endpoint.asset.AssetOut;
import se.leafcoders.rosette.endpoint.event.EventRefOut;
import se.leafcoders.rosette.endpoint.resource.ResourceRefOut;

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
