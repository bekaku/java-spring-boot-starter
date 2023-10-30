package com.grandats.api.givedeefive.model;

import com.grandats.api.givedeefive.annotation.GenSourceableTable;
import com.grandats.api.givedeefive.model.superclass.Auditable;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Sort;

import java.util.HashSet;
import java.util.Set;

@GenSourceableTable
@Getter
@Setter
@Entity
@Table(name = "files_directory", indexes = {
        @Index(columnList = "updated_user"),
        @Index(columnList = "created_user"),
})
public class FilesDirectory extends Auditable<Long> {

    @Column(length = 125)
    private String name;

    @Column(columnDefinition = "tinyint(1) default 1")
    boolean active;

    @OneToMany(mappedBy = "filesDirectoryParent", fetch = FetchType.LAZY)
    private Set<FilesDirectory> flesDirectories = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "files_directory_parent")
    private FilesDirectory filesDirectoryParent;

    public static Sort getSort() {
        return Sort.by(Sort.Direction.ASC, "name");
    }

}
