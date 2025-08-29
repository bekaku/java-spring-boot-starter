package com.bekaku.api.spring.model;

import com.bekaku.api.spring.annotation.GenSourceableTable;
import com.bekaku.api.spring.configuration.AuditListener;
import com.bekaku.api.spring.enumtype.PermissionType;
import com.bekaku.api.spring.model.superclass.Id;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Sort;

import java.util.HashSet;
import java.util.Set;

@GenSourceableTable(createPermission = false)
@Getter
@Setter
@Entity
@EntityListeners(AuditListener.class)
@NoArgsConstructor
@Table(name = "permission")
public class Permission extends Id {
    public Permission(String code) {
        this.code = code;
    }
    @Column(nullable = false, length = 125, unique = true)
    private String code;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    private String module;

    @Enumerated(EnumType.ORDINAL)
    private PermissionType operationType = PermissionType.CRUD;

    @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
    Set<AppRole> appRoles = new HashSet<>();

    @Override
    public String toString() {
        return "Permission{" +
                "code='" + code + '\'' +
                ", description='" + description + '\'' +
                ", operationType=" + operationType +
                ", id=" + getId() +
                '}';
    }

    public static Sort getSort() {
        return Sort.by(Sort.Direction.ASC, "code");
    }

    //JPA entity have a field not mapped to a DB column
//    @Transient
//    private String fieldNotEntity;

    /*
    static int transient1; // not persistent because of static
    final int transient2 = 0; // not persistent because of final
    transient int transient3; // not persistent because of transient
    @Transient int transient4; // not persistent because of @Transient
     */
}
