package se.leafcoders.rosette.persistence.model;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;

import se.leafcoders.rosette.exception.ApiString;
import se.leafcoders.rosette.permission.PermissionTreeHelper;

@Entity
@Table(name = "permissions")
public class Permission extends Persistable {

    public static Integer LEVEL_PUBLIC = 0;
    public static Integer LEVEL_ALL_USERS = 1;
    public static Integer LEVEL_GROUP = 2;
    public static Integer LEVEL_USER = 3;

    @NotEmpty(message = ApiString.STRING_NOT_EMPTY)
    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    private String name;

    @Column(nullable = false)
    @NotNull(message = ApiString.NOT_NULL)
    @Range(min = 0, max = 3, message = ApiString.NUMBER_OUT_OF_RANGE)
    private Integer level;

    // TODO NotNull when level 2 or 3
    private Long entityId;

    // TODO @ValidPermissions
    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    private String patterns;

    public Permission() {
    }

    public Permission(String name, Integer level, Long entityId, String patterns) {
        this.name = name;
        this.level = level;
        this.entityId = entityId;
        this.patterns = patterns;
    }

    // Getters and setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getPatterns() {
        return cleanPatterns(patterns);
    }

    public void setPatterns(String patterns) {
        this.patterns = cleanPatterns(patterns);
    }

    @Transient
    public List<String> getEachPattern() {
        return patterns != null ? Arrays.asList(patterns.split(PermissionTreeHelper.PERMISSION_DIVIDER)) : new LinkedList<String>();
    }

    private String cleanPatterns(String patternsToClean) {
        /*
         * TODO: if (patternsToClean != null) {
         * patternsToClean.removeAll(Arrays.asList("", null));
         * 
         * for (int index = 0; index < patternsToClean.size(); ++index) { String
         * permission = patternsToClean.get(index); while
         * (permission.endsWith(":*")) { permission = permission.substring(0,
         * permission.length() - 2); } patternsToClean.set(index, permission); }
         * }
         */
        return patternsToClean;
    }

}
