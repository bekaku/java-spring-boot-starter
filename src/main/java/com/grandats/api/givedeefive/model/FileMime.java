package com.grandats.api.givedeefive.model;

import com.grandats.api.givedeefive.annotation.GenSourceableTable;
import com.grandats.api.givedeefive.model.superclass.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@GenSourceableTable(createPermission=false,createDto=false, createController = false)
@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "file_mime")
public class FileMime extends Id {
    public FileMime(String name) {
        this.name = name;
    }

    @Column(length = 125)
    String name;
}
