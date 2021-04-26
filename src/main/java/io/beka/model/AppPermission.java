package io.beka.model;

import io.beka.annotation.GenSourceableTable;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;

@GenSourceableTable
@Getter
@Setter
@Entity
public class AppPermission extends BaseEntityId {

    @Column(length = 100, nullable = false)
    private String code;

    @Column(columnDefinition = "tinytext default null")
    private String description;

    @Column(length = 2)
    private String module;
}
