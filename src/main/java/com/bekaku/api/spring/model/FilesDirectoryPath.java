package com.bekaku.api.spring.model;

import com.bekaku.api.spring.annotation.GenSourceableTable;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Sort;

import java.io.Serializable;

@GenSourceableTable(createPermission = false, createController = false)
@Getter
@Setter
@Entity
@Table(name = "files_directory_path", comment = "Closure table for storing directory tree paths and hierarchy depth.")
@NoArgsConstructor
public class FilesDirectoryPath implements Serializable {

    public FilesDirectoryPath(FilesDirectoryPathId filesDirectoryPathId, int level) {
        this.filesDirectoryPathId = filesDirectoryPathId;
        this.level = level;
    }

    @EmbeddedId
    private FilesDirectoryPathId filesDirectoryPathId;

    @Column(name = "level", nullable = false, columnDefinition = "INT DEFAULT 0", comment = "Hierarchy distance/depth between ancestor and descendant (0 = self, 1 = direct child, etc.)")
    private int level = 0;

    public static Sort getSort() {
        return Sort.by(Sort.Direction.ASC, "level");
    }

}
