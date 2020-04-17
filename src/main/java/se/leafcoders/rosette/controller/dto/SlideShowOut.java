package se.leafcoders.rosette.controller.dto;

import java.util.List;

import lombok.Data;


@Data
public class SlideShowOut {

    private Long id;
    private String idAlias;
    private String name;
    private AssetFolderOut assetFolder;
    private List<SlideOut> slides;
}
