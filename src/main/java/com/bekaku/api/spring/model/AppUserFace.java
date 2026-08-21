package com.bekaku.api.spring.model;


import com.bekaku.api.spring.annotation.GenSourceableTable;
import com.bekaku.api.spring.model.superclass.Auditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;

@Getter
@Setter
@NoArgsConstructor
@GenSourceableTable(createController = false, createDto = false)
@Table(name = "app_user_face",
        comment = "Table for storing user face data."
)
@Entity
public class AppUserFace extends Auditable<Long> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_user", nullable = false)
    private AppUser appUser;

    // Store the vector as a string so that pgvector can convert it using cast(:vector as vector).
//    @Column(name = "embedding", columnDefinition = "vector(512)", nullable = false)
//    private String embedding;

    @ColumnTransformer(write = "?::vector")
    @Column(name = "embedding", columnDefinition = "vector(512)", nullable = false)
    private String embedding;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_manager",comment = "FK -> Ref table: file_manager (id). Reference to user image file")
    private FileManager fileManager;

}
