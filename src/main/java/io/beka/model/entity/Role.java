package io.beka.model.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Entity
public class Role extends BaseEntity {
    public Role(String name, String description) {
        this.name = name;
        this.description = description;
    }
    public void update(String name, String description, boolean status) {
        this.status = status;
        if (!"".equals(name)) {
            this.name = name;
        }
        if (!"".equals(description)) {
            this.description = description;
        }
    }

    @Column(length = 100)
    private String name;

    @Column(columnDefinition = "text default null")
    private String description;

    @Column(columnDefinition = "tinyint(1) default 1")
    private Boolean status;

    @ManyToMany(mappedBy = "roles")
    Set<User> users;

    @ManyToMany
    @JoinTable(
            name = "role_permission",
            joinColumns = @JoinColumn(name = "role"),
            inverseJoinColumns = @JoinColumn(name = "permission"))
    private Set<Permission> permissions;
}
