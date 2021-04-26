package io.beka.model;

import io.beka.annotation.GenSourceableTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Sort;

import javax.persistence.*;
import java.util.Set;

@GenSourceableTable
@NoArgsConstructor
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

    @Column(name = "name", length = 100, nullable = false, unique = true)
    private String name;

    @Column(name = "description", columnDefinition = "text default null")
    private String description;

    @Column(name = "status", columnDefinition = "tinyint(1) default 1")
    private Boolean status;

    @ManyToMany(mappedBy = "roles")
    Set<User> users;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "role_permission",
            joinColumns = @JoinColumn(name = "role"),
            inverseJoinColumns = @JoinColumn(name = "permission"))
    private Set<Permission> permissions;

    public static Sort getSort() {
        return Sort.by(Sort.Direction.ASC, "name");
    }
}