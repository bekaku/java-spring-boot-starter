package com.bekaku.api.spring.model;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Embeddable
public class FilesDirectoryPathId implements Serializable {

    private Long filesDirectory;
    private Long filesDirectoryParent;

    public FilesDirectoryPathId() {
    }

    public FilesDirectoryPathId(Long filesDirectory, Long filesDirectoryParent) {
        this.filesDirectory = filesDirectory;
        this.filesDirectoryParent = filesDirectoryParent;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
