package se.leafcoders.rosette.persistence.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

import org.hibernate.validator.constraints.Length;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import se.leafcoders.rosette.exception.ApiString;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "messages")
public class Message extends Persistable {

    private static final long serialVersionUID = -4746191929279339380L;

    @Column(name = "message_key")
    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Length(max = 32, message = ApiString.STRING_MAX_32_CHARS)
    private String key;

    @Column(name = "message_language")
    @Length(max = 8, message = ApiString.STRING_MAX_8_CHARS)
    private String language;

    @Length(max = 4000, message = ApiString.STRING_MAX_4000_CHARS)
    private String message;
}
