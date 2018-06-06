package se.leafcoders.rosette.persistence.model;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import se.leafcoders.rosette.exception.ApiString;

@Entity
@Table(name = "consents")
public class Consent extends Persistable {

    public enum Type { SIGNUP };
    public enum Source { WEBPAGE };
    
    @NotNull(message = ApiString.NOT_NULL)
    @Enumerated(EnumType.STRING)
    private Type type;

    @NotNull(message = ApiString.NOT_NULL)
    @Enumerated(EnumType.STRING)
    private Source source;

    @NotNull(message = ApiString.NOT_NULL)
    private LocalDateTime time;
    
    @NotNull(message = ApiString.NOT_NULL)
    private Long userId;

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Length(max = 4000, message = ApiString.STRING_MAX_4000_CHARS)
    private String consentText;

    
    // Getters and setters


    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getConsentText() {
        return consentText;
    }

    public void setConsentText(String consentText) {
        this.consentText = consentText;
    }

}
