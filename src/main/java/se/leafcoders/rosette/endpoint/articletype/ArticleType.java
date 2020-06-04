package se.leafcoders.rosette.endpoint.articletype;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
import se.leafcoders.rosette.endpoint.resourcetype.ResourceType;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "articletypes")
public class ArticleType extends Persistable {

    private static final long serialVersionUID = -2026041453379772684L;

    public enum RecordingStatus {
        NOT_EXPECTED, EXPECTING_RECORDING, HAS_RECORDING
    };

    @IdAlias
    @Column(nullable = false, unique = true)
    private String idAlias;

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    private String articlesTitle;

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    private String newArticleTitle;

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    private String articleSeriesTitle;

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    private String newArticleSerieTitle;

    @NotNull(message = ApiString.NOT_NULL)
    @ManyToOne
    @JoinColumn(name = "imagefolder_id")
    private AssetFolder imageFolder;

    @NotNull(message = ApiString.NOT_NULL)
    @ManyToOne
    @JoinColumn(name = "recordingfolder_id")
    private AssetFolder recordingFolder;

    @NotNull(message = ApiString.STRING_NOT_EMPTY)
    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private RecordingStatus defaultRecordingStatus;

    @NotNull(message = ApiString.NOT_NULL)
    @ManyToOne
    @JoinColumn(name = "author_resourcetype_id")
    private ResourceType authorResourceType;
}
