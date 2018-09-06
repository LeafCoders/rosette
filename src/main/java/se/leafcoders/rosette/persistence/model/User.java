package se.leafcoders.rosette.persistence.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Email;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotEmpty;
import org.springframework.data.annotation.Transient;
import se.leafcoders.rosette.exception.ApiString;

@Entity
@Table(name = "users")
public class User extends Persistable {

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
    
    private LocalDateTime lastLoginTime;
    
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "users")
    private List<Group> groups = new ArrayList<>();

    public User() {
    }

    // Getters and setters

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email != null ? email.toLowerCase() : null;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(LocalDateTime lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public List<Group> getGroups() {
        return groups;
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
