package se.leafcoders.rosette.persistence.model;

import javax.persistence.Embeddable;
import org.hibernate.validator.constraints.Length;
import se.leafcoders.rosette.exception.ApiString;

@Embeddable
public class HtmlContent {

    @Length(max = 10000, message = ApiString.STRING_MAX_10000_CHARS)
    private String contentRaw;

    @Length(max = 10000, message = ApiString.STRING_MAX_10000_CHARS)
    private String contentHtml;

    public HtmlContent() {
    }

    public HtmlContent(String contentRaw, String contentHtml) {
        this.contentRaw = contentRaw;
        this.contentHtml = contentHtml;
    }

    public String getContentRaw() {
        return contentRaw;
    }

    public void setContentRaw(String contentRaw) {
        this.contentRaw = contentRaw;
    }

    public String getContentHtml() {
        return contentHtml;
    }

    public void setContentHtml(String contentHtml) {
        this.contentHtml = contentHtml;
    }

}
