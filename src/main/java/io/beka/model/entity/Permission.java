package io.beka.model.entity;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Set;

@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonRootName("permision")
@Entity
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;

    @Basic(optional = false)
    @Column(nullable = false)
    private String name;

    @Basic(optional = false)
    private String description;

    @Basic(optional = false)
    private Boolean status=true;

    @Basic(optional = false)
    private String crudTable;

    @ManyToMany(mappedBy = "permissions")
    Set<Role> roles;

}
