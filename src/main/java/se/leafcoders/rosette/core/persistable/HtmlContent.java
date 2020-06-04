package se.leafcoders.rosette.core.persistable;

import javax.persistence.Embeddable;

import org.hibernate.validator.constraints.Length;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import se.leafcoders.rosette.core.exception.ApiString;

@Getter
@Setter
@NoArgsConstructor
@Embeddable
public class HtmlContent {

    @Length(max = 10000, message = ApiString.STRING_MAX_10000_CHARS)
    private String contentRaw;

    @Length(max = 10000, message = ApiString.STRING_MAX_10000_CHARS)
    private String contentHtml;

    public HtmlContent(String contentRaw, String contentHtml) {
        this.contentRaw = contentRaw;
        this.contentHtml = contentHtml;
    }
}
