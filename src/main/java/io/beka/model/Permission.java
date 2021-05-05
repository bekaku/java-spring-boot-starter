package io.beka.model;

import io.beka.annotation.GenSourceableTable;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Sort;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@GenSourceableTable
@Getter
@Setter
@Entity
public class Permission extends BaseId {

    @Column(nullable = false, length = 125)
    private String code;

    private String description;

    @Column(length = 100)
    private String module;

    @ManyToMany(mappedBy = "permissions")
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
