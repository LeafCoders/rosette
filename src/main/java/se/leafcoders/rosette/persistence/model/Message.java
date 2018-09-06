package se.leafcoders.rosette.persistence.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotEmpty;
import se.leafcoders.rosette.exception.ApiString;

@Entity
@Table(name = "messages")
public class Message extends Persistable {

    @Column(name = "message_key")
    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Length(max = 32, message = ApiString.STRING_MAX_32_CHARS)
    private String key;

    @Column(name = "message_language")
    @Length(max = 8, message = ApiString.STRING_MAX_8_CHARS)
    private String language;

    @Length(max = 4000, message = ApiString.STRING_MAX_4000_CHARS)
    private String message;


    // Getters and setters


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
