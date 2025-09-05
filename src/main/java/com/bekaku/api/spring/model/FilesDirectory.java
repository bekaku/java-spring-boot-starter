package com.bekaku.api.spring.model;

import com.bekaku.api.spring.annotation.GenSourceableTable;
import com.bekaku.api.spring.model.superclass.Auditable;

import com.bekaku.api.spring.util.DateUtil;
import com.bekaku.api.spring.util.SnowflakeIdHolder;
import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
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


    public void onUpdate(String name) {
        this.name = name;
        this.latestUpdated = DateUtil.getLocalDateTimeNow();
    }

    @Column(length = 125)
    private String name;

    boolean active = true;

    @OneToMany(mappedBy = "filesDirectoryParent", fetch = FetchType.LAZY)
    private Set<FilesDirectory> flesDirectories = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "files_directory_parent")
    private FilesDirectory filesDirectoryParent;

    private LocalDateTime latestUpdated;

    @Column(nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private long fileSize = 0;

    @Column(nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private long fileCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner")
    private AppUser owner;

    @PrePersist
    public void generateLatestUpdate() {
        if (this.latestUpdated == null) {
            this.latestUpdated = DateUtil.getLocalDateTimeNow();
        }
    }

    public static Sort getSort() {
        return Sort.by(Sort.Direction.ASC, "name");
    }

}
