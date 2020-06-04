package se.leafcoders.rosette.endpoint.asset;

import lombok.Data;

@Data
public class AssetOut {

    private Long id;
    private String type;
    private String mimeType;

    private String fileName;
    private String url;
    
    private boolean isImageFile;
    private boolean isAudioFile;
    private boolean isTextFile;

    private Long fileSize;
    private Long width;
    private Long height;
    private Long duration;
}
