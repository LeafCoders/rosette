package se.leafcoders.rosette.endpoint.permission;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import se.leafcoders.rosette.core.exception.ApiString;
import se.leafcoders.rosette.core.permission.PermissionTreeHelper;
import se.leafcoders.rosette.core.persistable.Persistable;
import se.leafcoders.rosette.core.validator.ValidPermissions;
import se.leafcoders.rosette.endpoint.permissionset.PermissionSet;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "permissions")
public class Permission extends Persistable {

    private static final long serialVersionUID = -1720660525271368229L;

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

    @ValidPermissions
    @Length(max = 200, message = ApiString.STRING_MAX_200_CHARS)
    private String patterns;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "permission_permissionsets", joinColumns = {
            @JoinColumn(name = "permission_id") }, inverseJoinColumns = {
                    @JoinColumn(name = "permissionset_id") }, uniqueConstraints = {
                            @UniqueConstraint(columnNames = { "permission_id", "permissionset_id" }) })
    private List<PermissionSet> permissionSets = new ArrayList<>();

    public Permission(String name, Integer level, Long entityId, String patterns) {
        this.name = name;
        this.level = level;
        this.entityId = entityId;
        this.patterns = patterns;
    }

    // Getters and setters

    public String getPatterns() {
        return cleanPatterns(patterns);
    }

    public void setPatterns(String patterns) {
        this.patterns = cleanPatterns(patterns);
    }

    @Transient
    public List<String> getEachPattern() {
        LinkedList<String> list = new LinkedList<>();
        Optional.ofNullable(getPatterns())
                .ifPresent(p -> list.addAll(Arrays.asList(patterns.split(PermissionTreeHelper.PERMISSION_DIVIDER))));
        getPermissionSets().forEach(ps -> list.addAll(ps.getEachPattern()));
        return list;
    }

    private String cleanPatterns(String patternsToClean) {
        if (patternsToClean != null) {
            patternsToClean = Arrays.stream(patternsToClean.split(",")).filter(p -> !p.isEmpty())
                    .collect(Collectors.joining(","));
        }
        return patternsToClean;
    }

    public List<PermissionSet> getPermissionSets() {
        if (permissionSets == null) {
            permissionSets = new ArrayList<>();
        }
        return permissionSets;
    }

    public void addPermissionSet(PermissionSet permissionSet) {
        getPermissionSets().add(permissionSet);
    }

    public void removePermissionSet(PermissionSet permissionSet) {
        getPermissionSets().remove(permissionSet);
    }

}
