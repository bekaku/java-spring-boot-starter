package io.beka.model;

import io.beka.annotation.GenSourceableTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;

@GenSourceableTable(createController = false)
@NoArgsConstructor
@Getter
@Setter
@Entity
public class UserAgent extends BaseEntityId {

    public UserAgent(String agent) {
        this.agent = agent;
    }

    @Column(nullable = false, length = 120)
    private String agent;
}
