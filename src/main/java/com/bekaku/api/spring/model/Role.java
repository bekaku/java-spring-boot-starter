package com.bekaku.api.spring.model;

import com.bekaku.api.spring.annotation.GenSourceableTable;
import com.bekaku.api.spring.configuration.AuditListener;
import com.bekaku.api.spring.model.superclass.SoftDeletedAuditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.domain.Sort;

import java.util.HashSet;
import java.util.Set;

@GenSourceableTable(createPermission = false, createFrontend = true)
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "role", indexes = {
        @Index(columnList = "deleted"),
        @Index(columnList = "updated_user"),
        @Index(columnList = "created_user"),
})
@SQLDelete(sql = "UPDATE role SET deleted = true WHERE id=?")
@SQLRestriction("deleted=false")
@EntityListeners(AuditListener.class)
public class Role extends SoftDeletedAuditable<Long> {

    public Role(String name, String nameEn, Boolean active, Boolean frontEnd) {
        this.name = name;
        this.nameEn = nameEn;
        this.active = active;
        this.frontEnd = frontEnd;
    }

    public void update(String name, String nameEn, Boolean active, Boolean frontEnd) {
        this.name = name;
        this.nameEn = nameEn;
        this.active = active;
        this.frontEnd = frontEnd;
    }

    @Column(name = "name", length = 125, nullable = false)
    private String name;

    @Column(name = "name_en", length = 125)
    private String nameEn;

    @Column(name = "active", columnDefinition = "tinyint(1) default 1")
    private Boolean active;

    @Column(name = "front_end", columnDefinition = "tinyint(1) default 0")
    private Boolean frontEnd;

    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    Set<User> users;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "role_permission",
            joinColumns = {@JoinColumn(name = "role")},
            inverseJoinColumns = {@JoinColumn(name = "permission")})
    private Set<Permission> permissions = new HashSet<>();

    @Override
    public String toString() {
        return "Role{" +
                "name='" + name + '\'' +
                ", nameEn='" + nameEn + '\'' +
                ", active=" + active +
                ", frontEnd=" + frontEnd +
                ", id=" + getId() +
                '}';
    }

    public static Sort getSort() {
        return Sort.by(Sort.Direction.ASC, "name");
    }
}
