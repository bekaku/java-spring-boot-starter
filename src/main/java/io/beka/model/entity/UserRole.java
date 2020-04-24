package io.beka.model.entity;

import lombok.Getter;

import javax.persistence.*;
import java.io.Serializable;


@Getter
@Entity
public class UserRole implements Serializable {

    @Id
    @ManyToOne
    @JoinColumn(name = "user")
    private User user;

    @Id
    private Long role;
}
