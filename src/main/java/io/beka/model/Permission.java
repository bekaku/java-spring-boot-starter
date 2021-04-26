package io.beka.model;

import io.beka.annotation.GenSourceableTable;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Sort;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.Set;

@GenSourceableTable
@Getter
@Setter
@Entity
public class Permission extends BaseEntity {
    @NotEmpty
    @Column(nullable = false, length = 125)
    private String name;

    @Basic(optional = false)
    private String description;

    @Column(columnDefinition = "tinyint(1) default 1", nullable = false)
    private Boolean status = true;

    @Basic(optional = false)
    @Column(length = 100)
    private String crudTable;

    @ManyToMany(mappedBy = "permissions")
    Set<Role> roles;

    public static Sort getSort(){
        return Sort.by(Sort.Direction.DESC, "name");
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
