package se.leafcoders.rosette.endpoint.asset;

import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import lombok.Data;
import se.leafcoders.rosette.core.exception.ApiString;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class AssetFileIn {

    private MultipartFile file;
    private Long folderId;

    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    @Pattern(regexp = "^[\\w._-]+.[\\w]$", message = ApiString.FILENAME_INVALID)
    private String fileName;
}
