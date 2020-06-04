package se.leafcoders.rosette.endpoint.slideshow;

import java.util.List;

import lombok.Data;
import se.leafcoders.rosette.endpoint.assetfolder.AssetFolderOut;


@Data
public class SlideShowOut {

    private Long id;
    private String idAlias;
    private String name;
    private AssetFolderOut assetFolder;
    private List<SlideOut> slides;
}
