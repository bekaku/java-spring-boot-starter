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
    @Column(length = 100, unique = true)
    private String username;

    private String password;

    @Column(length = 125, unique = true, nullable = false)
    private String email;

    @Enumerated(EnumType.ORDINAL)
    private AppLocale defaultLocale;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "avatar_file_id")
    private FileManager avatarFile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cover_file_id")
    private FileManager coverFile;

    private String salt;

    @Column(nullable = false)
    private boolean active = true;

//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
//    private List<AccessToken> accessTokens;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "app_user_role",
            joinColumns = @JoinColumn(name = "app_user"),
            inverseJoinColumns = @JoinColumn(name = "app_role"))
    private Set<AppRole> appRoles = new HashSet<>();

    public static Sort getSort() {
        return Sort.by(Sort.Direction.ASC, "username");
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
