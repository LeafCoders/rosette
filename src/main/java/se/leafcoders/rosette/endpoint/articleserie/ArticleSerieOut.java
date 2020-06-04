package se.leafcoders.rosette.endpoint.articleserie;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;
import se.leafcoders.rosette.core.converter.RosetteDateTimeJsonSerializer;
import se.leafcoders.rosette.endpoint.asset.AssetOut;

@Data
public class ArticleSerieOut {

    private Long id;
    private String idAlias;
    private Long articleTypeId;
    private String title;
    private String contentRaw;
    private String contentHtml;
    private AssetOut image;
    @JsonSerialize(using = RosetteDateTimeJsonSerializer.class)
    private LocalDateTime lastUseTime;
}
