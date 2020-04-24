package io.beka.model.entity;

import lombok.Getter;

import javax.persistence.*;
import java.util.Set;

@Getter
@Entity
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @Column(columnDefinition = "text default null")
    private String description;

    @Column(columnDefinition = "tinyint(1) default 1")
    private Boolean status;

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
    private Set<PermissionRole> permissionRoles;
}
