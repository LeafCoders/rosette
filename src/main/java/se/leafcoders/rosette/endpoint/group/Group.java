package se.leafcoders.rosette.endpoint.group;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotEmpty;

import org.hibernate.validator.constraints.Length;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import se.leafcoders.rosette.core.exception.ApiString;
import se.leafcoders.rosette.core.persistable.Persistable;
import se.leafcoders.rosette.core.validator.IdAlias;
import se.leafcoders.rosette.endpoint.user.User;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "groups")
public class Group extends Persistable {

    private static final long serialVersionUID = 3945087664808860374L;

    @IdAlias
    @Column(nullable = false, unique = true)
    private String idAlias;

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    @Column(nullable = false, unique = true)
    private String name;

    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    private String description;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "group_users", joinColumns = { @JoinColumn(name = "group_id") }, inverseJoinColumns = { @JoinColumn(name = "user_id") },
        uniqueConstraints = { @UniqueConstraint(columnNames = { "group_id", "user_id" }) }
    )
    private List<User> users = new ArrayList<>();

    // Getters and setters

    public List<User> getUsers() {
        if (users == null) {
            users = new ArrayList<>();
        }
        users.sort((a, b) -> a.getFullName().compareTo(b.getFullName()));
        return users;
    }

    public void addUser(User user) {
        getUsers().add(user);
    }

    public void removeUser(User user) {
        getUsers().remove(user);
    }
}
