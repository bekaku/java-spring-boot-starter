package com.bekaku.api.spring.model;

import com.bekaku.api.spring.annotation.GenSourceableTable;
import com.bekaku.api.spring.model.superclass.SoftDeletedAuditableCreated;
import com.bekaku.api.spring.util.FileUtil;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.domain.Sort;

@GenSourceableTable
@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "file_manager", indexes = {
        @Index(columnList = "deleted"),
        @Index(columnList = "created_user"),
})
@SQLDelete(sql = "UPDATE file_manager SET deleted = true, files_directory_id = null WHERE id=?")
@SQLRestriction("deleted=false")
public class FileManager extends SoftDeletedAuditableCreated<Long> {

    public FileManager(String fileName, String originalFileName, long fileSize, FileMime fileMime, String filePath) {
        this.fileName = fileName;
        this.originalFileName = originalFileName;
        this.fileSize = fileSize;
        this.fileMime = fileMime;
        this.filePath = filePath;
    }

    public void onDelete() {
        this.filePath = null;
        this.fileSize = 0;
        this.originalFileName = null;
        this.fileName = null;
    }

    @Column
    private String fileName;

    @Column(length = 125)
    private String originalFileName;

    @Column
    private long fileSize = 0;

    @Column
    private String filePath;

    boolean readable = true;

    boolean writeable = true;

    boolean locked = false;

    boolean hidden = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "files_directory_id")
    private FilesDirectory filesDirectory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_mime_id")
    private FileMime fileMime;

    public boolean isImage() {
        return FileUtil.isImage(getFileMime().getName());
    }

    public static Sort getSort() {
        return Sort.by(Sort.Direction.ASC, "originalFileName");
    }

}
