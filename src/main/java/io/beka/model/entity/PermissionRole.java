package io.beka.model.entity;

import lombok.Getter;
import org.hibernate.annotations.ManyToAny;

import javax.persistence.*;
import java.io.Serializable;


@Getter
@Entity
public class PermissionRole implements Serializable {

    @Id
    @ManyToOne
    @JoinColumn(name = "role")
    private Role role;

    @Id
    private Long permission;
}
