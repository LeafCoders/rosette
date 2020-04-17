package se.leafcoders.rosette.controller.dto;

import java.time.LocalDateTime;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;
import se.leafcoders.rosette.persistence.converter.RosetteDateTimeJsonSerializer;

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
