package com.bekaku.api.spring.model;


import com.bekaku.api.spring.annotation.GenSourceableTable;
import com.bekaku.api.spring.model.superclass.SoftDeletedAuditableCreated;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Setter
@NoArgsConstructor
@GenSourceableTable(createController = false, createDto = false, createPermission = false)
@Table(name = "identity_link",
        comment = "Table for linking user accounts to identity groups.",
        indexes = {
                @Index(columnList = "deleted")
        })
@SQLDelete(sql = "UPDATE identity_link SET deleted = true WHERE id=?")
@SQLRestriction("deleted=false")
@Entity
public class IdentityLink extends SoftDeletedAuditableCreated<Long> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "identity_group", nullable = false,
            comment = "FK -> Ref table: identity_group (id). The associated identity group")
    private IdentityGroup identityGroup;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_user", unique = true, nullable = false,
            comment = "FK -> Ref table: app_user (id). The associated user account")
    private AppUser appUser;
}
