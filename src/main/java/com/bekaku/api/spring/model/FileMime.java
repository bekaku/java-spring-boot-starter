package com.bekaku.api.spring.model;

import com.bekaku.api.spring.annotation.GenSourceableTable;
import com.bekaku.api.spring.model.superclass.Id;
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
@Table(name = "file_mime", comment = "Table for storing supported file MIME types.")
public class FileMime extends Id {
    public FileMime(String name) {
        this.name = name;
    }

    @Column(name = "name", length = 125, comment = "MIME type identifier string (e.g., image/png, application/pdf)")
    private String name;
}
