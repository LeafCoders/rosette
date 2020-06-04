package se.leafcoders.rosette.endpoint.asset;

import java.util.Arrays;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import se.leafcoders.rosette.core.exception.ApiString;
import se.leafcoders.rosette.core.persistable.Persistable;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "assets")
public class Asset extends Persistable {

    private static final long serialVersionUID = -3399394909555493046L;

    public enum AssetType {
        FILE, URL
    };

    @NotNull(message = ApiString.STRING_NOT_EMPTY)
    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private AssetType type;

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    @Column(nullable = false)
    private String mimeType;

    @NotNull(message = ApiString.NOT_NULL)
    private Long folderId;

    @Column(unique = true)
    private String fileId;

    private Integer fileVersion;
    
    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    @Pattern(regexp = "^[\\w._-]+.[\\w]$", message = ApiString.FILENAME_INVALID)
    private String fileName;    // File name in server file system

    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    @URL
    private String url;         // URL to external asset 

    private Long fileSize;      // (bytes)
    private Long width;         // Image
    private Long height;        // Image
    private Long duration;      // Audio (milliseconds)

    // Getters and setters

    public String fileNameOnDisk() {
        if (fileVersion != null) {
            return fileId.replaceFirst("-", "-" + String.format("%04d", fileVersion) + "-");
        } else {
            // Stored files before "fileVersion" were added has no version info in its filename on disk
            return fileId;
        }
    }
    
    public boolean isImageFile() {
        return mimeType.startsWith("image/");
    }
    
    public boolean isAudioFile() {
        return mimeType.startsWith("audio/");
    }
    
    public boolean isTextFile() {
        return Arrays.asList(
                "text/",
                "application/json",
                "application/xml",
                "application/xhtml+xml",
                "application/html",
                "application/javascript",
                "application/x-javascript",
                "application/ecmascript"
        ).stream().anyMatch(type -> mimeType.startsWith(type));
    }
}
