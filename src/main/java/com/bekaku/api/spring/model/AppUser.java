package com.bekaku.api.spring.model;

import com.bekaku.api.spring.annotation.GenSourceableTable;
import com.bekaku.api.spring.enumtype.AppLocale;
import com.bekaku.api.spring.model.superclass.Id;
import com.bekaku.api.spring.model.superclass.SoftDeletedAuditable;
import com.bekaku.api.spring.util.UuidUtils;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.domain.Sort;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@GenSourceableTable(createPermission = false)
@Getter
@Setter
@Entity
@Table(name = "app_user",
        comment = "Table for storing user account information.",
        indexes = {
                @Index(columnList = "active"),
                @Index(columnList = "deleted"),
                @Index(columnList = "updated_user"),
                @Index(columnList = "created_user"),
        }
)
@SQLDelete(sql = "UPDATE app_user SET deleted = true WHERE id=?")
@SQLRestriction("deleted=false")
public class AppUser extends SoftDeletedAuditable<Long> {

    public void addNew(String username, String password, String email, Boolean active) {
        this.salt = UuidUtils.generateUUID().toString();
        this.username = username;
        this.password = password;
        this.email = email;
        this.active = active;
        this.defaultLocale = AppLocale.th;
    }

    public void update(String username, String email, Boolean status) {
        if (!"".equals(email)) {
            this.email = email;
        }
        this.active = status;
        if (!"".equals(username)) {
            this.username = username;
        }else{
            this.username = null;
        }
    }

    public String getCurrentLocale() {
        return getDefaultLocale() != null ? getDefaultLocale().toString() : AppLocale.th.toString();
    }

    //    @Basic(optional = false)
//    @Column(nullable = false, length = 100, unique = true)
    @Column(length = 100, unique = true, comment = "Unique username for user authentication")
    private String username;

    @Column(comment = "Hashed password for user authentication")
    private String password;

    @Column(length = 125, unique = true, nullable = false, comment = "Unique email address of the user")
    private String email;

    @Enumerated(EnumType.ORDINAL)
    @Column(comment = "Default language/locale setting for the user")
    private AppLocale defaultLocale;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "avatar_file_id",comment = "FK -> Ref table: file_manager (id). Reference to user avatar image file")
    private FileManager avatarFile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cover_file_id", comment = "FK -> Ref table: file_manager (id). Reference to user cover image file")
    private FileManager coverFile;

    @Column(comment = "Cryptographic salt used for password hashing")
    private String salt;

    @Column(nullable = false, comment = "Account status flag (true = active, false = disabled/suspended)")
    private boolean active = true;

//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
//    private List<AccessToken> accessTokens;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "app_user_role",
            joinColumns = @JoinColumn(name = "app_user", comment = "FK -> Ref table: app_user (id). User identifier"),
            inverseJoinColumns = @JoinColumn(name = "app_role", comment = "FK -> Ref table: app_role (id). Role identifier"))
    private Set<AppRole> appRoles = new HashSet<>();

    public static Sort getSort() {
        return Sort.by(Sort.Direction.ASC, "username");
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
