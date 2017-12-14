package se.leafcoders.rosette.persistence.model;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import org.hibernate.validator.constraints.NotEmpty;
import se.leafcoders.rosette.exception.ApiString;

@Entity
@Table(name = "slideshows")
public class SlideShow extends Persistable {

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Pattern(regexp = "[a-z][a-zA-Z0-9]+", message = ApiString.IDALIAS_INVALID_FORMAT)
    @Column(nullable = false, unique = true)
    private String idAlias;

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "assetfolder_id", nullable = false, insertable = false, updatable = false)
    protected Long assetFolderId;

    @NotNull(message = ApiString.NOT_NULL)
    @ManyToOne
    @JoinColumn(name = "assetfolder_id")
    protected AssetFolder assetFolder;

    @OneToMany(mappedBy = "slideShow", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Slide> slides;

    public SlideShow() {
    }

    // Getters and setters

    public String getIdAlias() {
        return idAlias;
    }

    public void setIdAlias(String idAlias) {
        this.idAlias = idAlias;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getAssetFolderId() {
        return assetFolderId;
    }

    public void setAssetFolderId(Long assetFolderId) {
        this.assetFolderId = assetFolderId;
    }

    public AssetFolder getAssetFolder() {
        return assetFolder;
    }

    public void setAssetFolder(AssetFolder assetFolder) {
        this.assetFolder = assetFolder;
        this.setAssetFolderId(assetFolder != null ? assetFolder.getId() : null);
    }

    public List<Slide> getSlides() {
        if (slides == null) {
            slides = new ArrayList<Slide>();
        }
        return slides;
    }

    public void setSlides(List<Slide> slides) {
        this.slides = slides;
    }

    public void addSlide(Slide slide) {
        getSlides().add(slide);
        slide.slideShow = this;
        slide.slideShowId = this.id;
    }
}
