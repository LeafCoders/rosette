package se.leafcoders.rosette.endpoint.auth;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.hibernate.validator.constraints.Length;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import se.leafcoders.rosette.core.converter.RosetteDateTimeJsonDeserializer;
import se.leafcoders.rosette.core.converter.RosetteDateTimeJsonSerializer;
import se.leafcoders.rosette.core.exception.ApiString;
import se.leafcoders.rosette.core.persistable.Persistable;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "consents")
public class Consent extends Persistable {

    private static final long serialVersionUID = 746216170337628851L;

    public enum Type {
        SIGNUP
    };

    public enum Source {
        WEBPAGE
    };

    @NotNull(message = ApiString.NOT_NULL)
    @Enumerated(EnumType.STRING)
    private Type type;

    @NotNull(message = ApiString.NOT_NULL)
    @Enumerated(EnumType.STRING)
    private Source source;

    @NotNull(message = ApiString.NOT_NULL)
    @JsonDeserialize(using = RosetteDateTimeJsonDeserializer.class)
    @JsonSerialize(using = RosetteDateTimeJsonSerializer.class)
    private LocalDateTime time;

    @NotNull(message = ApiString.NOT_NULL)
    private Long userId;

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Length(max = 4000, message = ApiString.STRING_MAX_4000_CHARS)
    private String consentText;
}
