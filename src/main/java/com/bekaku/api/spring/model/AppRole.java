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
@Table(name = "app_role", indexes = {
        @Index(columnList = "deleted"),
        @Index(columnList = "updated_user"),
        @Index(columnList = "created_user"),
})
@SQLDelete(sql = "UPDATE app_role SET deleted = true WHERE id=?")
@SQLRestriction("deleted=false")
@EntityListeners(AuditListener.class)
public class AppRole extends SoftDeletedAuditable<Long> {

    public AppRole(String name, Boolean active) {
        this.name = name;
        this.active = active;
    }

    public void update(String name, Boolean active) {
        this.name = name;
        this.active = active;
    }

    @Column(name = "name", length = 125, nullable = false)
    private String name;

    private Boolean active = true;

    @ManyToMany(mappedBy = "appRoles", fetch = FetchType.LAZY)
    Set<AppUser> appUsers;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "role_permission",
            joinColumns = {@JoinColumn(name = "app_role")},
            inverseJoinColumns = {@JoinColumn(name = "permission")})
    private Set<Permission> permissions = new HashSet<>();

    @Override
    public String toString() {
        return "Role{" +
                "name='" + name + '\'' +
                ", active=" + active +
                ", id=" + getId() +
                '}';
    }

    public static Sort getSort() {
        return Sort.by(Sort.Direction.ASC, "name");
    }
}
