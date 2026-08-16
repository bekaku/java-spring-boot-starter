package com.bekaku.api.spring.model;

import com.bekaku.api.spring.annotation.GenSourceableTable;
import com.bekaku.api.spring.model.superclass.Auditable;

import com.bekaku.api.spring.model.superclass.SoftDeletedAuditable;
import com.bekaku.api.spring.util.DateUtil;
import com.bekaku.api.spring.util.SnowflakeIdHolder;
import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@GenSourceableTable
@Getter
@Setter
@Entity
@Table(name = "files_directory",
        comment = "Table for storing file directory and folder hierarchy structure.",
        indexes = {
        @Index(columnList = "updated_user"),
        @Index(columnList = "created_user"),
})
@SQLDelete(sql = "UPDATE files_directory SET deleted = true WHERE id=?")
@SQLRestriction("deleted=false")
public class FilesDirectory extends SoftDeletedAuditable<Long> {


    public void onUpdate(String name) {
        this.name = name;
        this.latestUpdated = DateUtil.getLocalDateTimeNow();
    }

    @Column(name = "name", length = 125, comment = "Directory or folder name")
    private String name;

    @Column(name = "active", comment = "Flag indicating if the directory is active")
    boolean active = true;

    @OneToMany(mappedBy = "filesDirectoryParent", fetch = FetchType.LAZY)
    private Set<FilesDirectory> flesDirectories = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "files_directory_parent", comment = "FK -> Ref table: files_directory (id). Self-reference to parent directory")
    private FilesDirectory filesDirectoryParent;

    @Column(name = "latest_updated", comment = "Timestamp of the latest update to this directory")
    private LocalDateTime latestUpdated;

    @Column(name = "file_size", nullable = false, columnDefinition = "BIGINT DEFAULT 0", comment = "Total aggregated file size in bytes inside this directory")
    private long fileSize = 0;

    @Column(name = "file_count", nullable = false, columnDefinition = "BIGINT DEFAULT 0", comment = "Total count of files stored inside this directory")
    private long fileCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner", comment = "FK -> Ref table: app_user (id). The user who owns this directory")
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
