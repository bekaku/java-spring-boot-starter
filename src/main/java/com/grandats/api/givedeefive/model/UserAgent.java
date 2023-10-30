package com.grandats.api.givedeefive.model;

import com.grandats.api.givedeefive.annotation.GenSourceableTable;
import com.grandats.api.givedeefive.model.superclass.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@GenSourceableTable(createController = false, createDto = false, createPermission = false)
@NoArgsConstructor
@Getter
@Setter
@Table(name = "user_agent", indexes = {
        @Index(columnList = "agent"),
})
@Entity
public class UserAgent extends Id {

    public UserAgent(String agent) {
        this.agent = agent;
    }

    @Column(nullable = false)
    private String agent;
}
