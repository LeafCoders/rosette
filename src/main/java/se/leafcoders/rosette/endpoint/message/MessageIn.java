package se.leafcoders.rosette.endpoint.message;

import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.hibernate.validator.constraints.Length;

import lombok.Data;
import se.leafcoders.rosette.core.exception.ApiString;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class MessageIn {

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Length(max = 32, message = ApiString.STRING_MAX_32_CHARS)
    private String key;

    @Length(max = 8, message = ApiString.STRING_MAX_8_CHARS)
    private String language;

    @Length(max = 4000, message = ApiString.STRING_MAX_4000_CHARS)
    private String message;
}
