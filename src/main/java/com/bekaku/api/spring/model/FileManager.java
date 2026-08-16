package com.bekaku.api.spring.model;

import com.bekaku.api.spring.annotation.GenSourceableTable;
import com.bekaku.api.spring.model.superclass.SoftDeletedAuditable;
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
@Table(name = "file_manager",
        comment = "Table for storing uploaded files and metadata.",
        indexes = {
        @Index(columnList = "deleted"),
        @Index(columnList = "created_user"),
})
@SQLDelete(sql = "UPDATE file_manager SET deleted = true, files_directory_id = null WHERE id=?")
@SQLRestriction("deleted=false")
public class FileManager extends SoftDeletedAuditable<Long> {

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

    @Column(comment = "Stored system file name on server or storage")
    private String fileName;

    @Column(length = 125, comment = "Original uploaded file name")
    private String originalFileName;

    @Column(comment = "File size in bytes")
    private long fileSize = 0;

    @Column(comment = "Storage path or directory where the file is stored")
    private String filePath;

    @Column(comment = "Flag indicating if the file has read permission")
    boolean readable = true;

    @Column(comment = "Flag indicating if the file has write permission")
    boolean writeable = true;

    @Column(comment = "Flag indicating if the file is locked to prevent modification")
    boolean locked = false;

    @Column(comment = "Flag indicating if the file is hidden from standard view")
    boolean hidden = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "files_directory_id", comment = "FK -> Ref table: files_directory (id). Directory containing this file")
    private FilesDirectory filesDirectory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_mime_id", comment = "FK -> Ref table: file_mime (id). MIME type reference of the file")
    private FileMime fileMime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner", comment = "FK -> Ref table: app_user (id). The user who owns this file")
    private AppUser owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "thumbnail_file", comment = "FK -> Ref table: file_manager (id). Self-reference to thumbnail file")
    private FileManager thumbnailFile;

    @Column(columnDefinition = "INT DEFAULT 0", comment = "Media duration in seconds (for audio/video files)")
    private int duration;

    @Column(length = 125, comment = "Display title or custom label for the file")
    private String title;

    @Column(columnDefinition = "TEXT", comment = "Detailed description or notes about the file")
    private String description;

    @Column(columnDefinition = "boolean default false", comment = "Flag indicating whether a thumbnail is generated/used")
    private boolean useThumbnail;

    public boolean isImage() {
        return FileUtil.isImage(getFileMime().getName());
    }

    public static Sort getSort() {
        return Sort.by(Sort.Direction.ASC, "originalFileName");
    }

}
