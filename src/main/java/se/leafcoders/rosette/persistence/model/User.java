package se.leafcoders.rosette.persistence.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Transient;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import se.leafcoders.rosette.exception.ApiString;
import se.leafcoders.rosette.persistence.converter.RosetteDateTimeJsonDeserializer;
import se.leafcoders.rosette.persistence.converter.RosetteDateTimeJsonSerializer;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User extends Persistable {

    private static final long serialVersionUID = -8971046618930183647L;

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    @Email(message = ApiString.EMAIL_INVALID)
    @Column(nullable = false, unique = true)
    private String email;

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    @Column(nullable = false)
    private String firstName;

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    @Column(nullable = false)
    private String lastName;

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Column(length = 60)
    private String password;

    @NotNull
    private Boolean isActive;
    
    @JsonDeserialize(using = RosetteDateTimeJsonDeserializer.class)
    @JsonSerialize(using = RosetteDateTimeJsonSerializer.class)
    private LocalDateTime lastLoginTime;
    
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "users")
    private List<Group> groups = new ArrayList<>();

    // Getters and setters

    public void setEmail(String email) {
        this.email = email != null ? email.toLowerCase() : null;
    }

    // Helpers

    @Transient
    public String getFullName() {
        String name = "";

        String delimiter = "";

        if (this.getFirstName() != null) {
            name = this.getFirstName();
            delimiter = " ";
        }

        if (this.getLastName() != null) {
            name += delimiter + this.getLastName();
        }

        return name;
    }

    @Transient
    public void setFullName(String fullName) {
        // nothing
    }

}
