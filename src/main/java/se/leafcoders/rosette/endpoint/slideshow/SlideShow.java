package se.leafcoders.rosette.endpoint.slideshow;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import se.leafcoders.rosette.core.exception.ApiString;
import se.leafcoders.rosette.core.persistable.Persistable;
import se.leafcoders.rosette.core.validator.IdAlias;
import se.leafcoders.rosette.endpoint.assetfolder.AssetFolder;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "slideshows")
public class SlideShow extends Persistable {

    private static final long serialVersionUID = -4815639774048738241L;

    @IdAlias
    @Column(nullable = false, unique = true)
    private String idAlias;

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
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

    // Getters and setters

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

    public void addSlide(Slide slide) {
        getSlides().add(slide);
        slide.slideShow = this;
        slide.slideShowId = this.id;
    }
}
