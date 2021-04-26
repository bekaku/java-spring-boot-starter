package io.beka.model;

import io.beka.annotation.GenSourceableTable;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Sort;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotEmpty;
import java.util.Set;

@GenSourceableTable
@Getter
@Setter
@Entity
public class Permission extends BaseEntityId {
    @NotEmpty
    @Column(nullable = false, length = 125)
    private String code;

    private String description;

    @Column(length = 100)
    private String module;

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
