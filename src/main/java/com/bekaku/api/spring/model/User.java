package com.bekaku.api.spring.model;

import com.bekaku.api.spring.annotation.GenSourceableTable;
import com.bekaku.api.spring.enumtype.AppLocale;
import com.bekaku.api.spring.model.superclass.SoftDeletedAuditable;
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
import java.util.UUID;

@GenSourceableTable(createPermission = false)
@Getter
@Setter
@Entity
@Table(name = "user",
        indexes = {
                @Index(columnList = "active"),
                @Index(columnList = "deleted"),
                @Index(columnList = "updated_user"),
                @Index(columnList = "created_user"),
        }
)
@SQLDelete(sql = "UPDATE user SET deleted = true WHERE id=?")
@SQLRestriction("deleted=false")
public class User extends SoftDeletedAuditable<Long> {

    public void addNew(String username, String password, String email, Boolean active) {
        this.salt = UUID.randomUUID().toString();
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
        }
    }

    public String getCurrentLocale() {
        return getDefaultLocale() != null ? getDefaultLocale().toString() : AppLocale.th.toString();
    }

    //    @Basic(optional = false)
//    @Column(nullable = false, length = 100, unique = true)
    @Column(length = 100, unique = true)
    private String username;

    private String password;

    @Column(length = 125, unique = true, nullable = false)
    private String email;

    @Column(length = 1, columnDefinition = "tinyint(1) default 0")
    private AppLocale defaultLocale;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "avatar_file_id")
    private FileManager avatarFile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cover_file_id")
    private FileManager coverFile;

    private String salt;

    @Column(columnDefinition = "tinyint(1) default 1", nullable = false)
    private boolean active;

//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
//    private List<AccessToken> accessTokens;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user"),
            inverseJoinColumns = @JoinColumn(name = "role"))
    private Set<Role> roles = new HashSet<>();


    public static Sort getSort() {
        return Sort.by(Sort.Direction.ASC, "username");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User user = (User) o;
        return getId() != null && Objects.equals(getId(), user.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
