package se.leafcoders.rosette.persistence.model;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;

import org.hibernate.validator.constraints.Length;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import se.leafcoders.rosette.exception.ApiString;
import se.leafcoders.rosette.permission.PermissionTreeHelper;
import se.leafcoders.rosette.persistence.validator.ValidPermissions;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "permissionsets")
public class PermissionSet extends Persistable {

    private static final long serialVersionUID = 5876789649492726399L;

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    private String name;

    @ValidPermissions
    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    private String patterns;

    // Getters and setters

    public String getPatterns() {
        return cleanPatterns(patterns);
    }

    public void setPatterns(String patterns) {
        this.patterns = cleanPatterns(patterns);
    }

    @Transient
    public List<String> getEachPattern() {
        return patterns != null ? Arrays.asList(patterns.split(PermissionTreeHelper.PERMISSION_DIVIDER))
                : new LinkedList<String>();
    }

    private String cleanPatterns(String patternsToClean) {
        if (patternsToClean != null) {
            patternsToClean = Arrays.stream(patternsToClean.split(",")).filter(p -> !p.isEmpty())
                    .collect(Collectors.joining(","));
        }
        return patternsToClean;
    }

}
