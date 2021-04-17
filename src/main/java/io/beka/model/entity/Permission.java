package io.beka.model.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Sort;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.util.Set;

@Getter
@Setter
@Entity
public class Permission extends BaseEntityWithAudit {
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
//    @GenericGenerator(name = "native", strategy = "native")
//    private Long id;

    @Basic(optional = false)
    @Column(nullable = false)
    private String name;

    @Basic(optional = false)
    private String description;

    @Column(columnDefinition = "tinyint(1) default 1")
    private Boolean status = true;

    @Basic(optional = false)
    private String crudTable;

    @ManyToMany(mappedBy = "permissions")
    Set<Role> roles;

    public static Sort getSort(){
        return Sort.by(Sort.Direction.DESC, "name");
    }

}
