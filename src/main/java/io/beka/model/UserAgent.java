package io.beka.model;

import io.beka.annotation.GenSourceableTable;
import javax.persistence.Column;
import javax.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@GenSourceableTable(createController = false)
@NoArgsConstructor
@Getter
@Setter
@Entity
public class UserAgent extends BaseId {

    public UserAgent(String agent) {
        this.agent = agent;
    }

    @Column(nullable = false, length = 120)
    private String agent;
}
