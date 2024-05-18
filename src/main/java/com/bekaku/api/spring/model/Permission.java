package com.bekaku.api.spring.model;

import com.bekaku.api.spring.annotation.GenSourceableTable;
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
@NoArgsConstructor
@Table(name = "permission")
public class Permission extends Id {
    public Permission(String code, Boolean frontEnd) {
        this.code = code;
        this.frontEnd = frontEnd;
    }

    @Column(nullable = false, length = 125, unique = true)
    private String code;

    @Column(name = "remark", columnDefinition = "text default null")
    private String remark;

//    private String module;

    @Column(name = "front_end", columnDefinition = "tinyint(1) default 1")
    private Boolean frontEnd;

    /**
     * 1=crud, 2=report, 3=other
     */
    @Column(columnDefinition = "tinyint(0) default 1")
    private int operationType = 1;

    @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
    Set<Role> roles = new HashSet<>();

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
