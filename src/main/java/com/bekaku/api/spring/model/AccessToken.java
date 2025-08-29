package com.bekaku.api.spring.model;

import com.bekaku.api.spring.annotation.GenSourceableTable;
import com.bekaku.api.spring.enumtype.AccessTokenServiceType;
import com.bekaku.api.spring.model.superclass.Id;
import com.bekaku.api.spring.util.DateUtil;
import com.bekaku.api.spring.util.UuidUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;

import static jakarta.persistence.FetchType.LAZY;


@Getter
@Setter
@NoArgsConstructor
@GenSourceableTable(createController = false, createDto = false, createPermission = false)
@Table(name = "access_token",
        indexes = {
                @Index(columnList = "revoked"),
                @Index(columnList = "fcmEnable"),
                @Index(columnList = "fcmToken"),
                @Index(columnList = "lastest_active"),
        }
)
@Entity
public class AccessToken extends Id {

    public AccessToken(AppUser appUser, Date expiresAt, boolean revoked, ApiClient apiClient,
                       LoginLog loginLog, LocalDateTime createdDate, String fcmToken) {
        this.token = UuidUtils.generateUUID().toString();
        this.appUser = appUser;
        this.expiresAt = expiresAt;
        this.revoked = revoked;
        this.apiClient = apiClient;
        this.loginLog = loginLog;
        this.createdDate = createdDate;
        this.fcmToken = fcmToken;
        this.fcmEnable = true;
        this.lastestActive = DateUtil.getLocalDateTimeNow();
    }
    public void onCreateToken(AppUser appUser, Date expiresAt, String token, AccessTokenServiceType service) {
        this.token = token;
        this.appUser = appUser;
        this.expiresAt = expiresAt;
        this.revoked = false;
        this.createdDate = DateUtil.getLocalDateTimeNow();
        this.fcmEnable = false;
        this.service = service;
        this.newToken = true;
    }

    //    @PrePersist
    //    public void prePersist() {
    //        if (uniqeId == null) {
    //            uniqeId = UuidUtils.generateUUID();
    //        }
    //    }
//    @Column(columnDefinition = "BINARY(16)")
//    @GeneratedUuidV7
//    @Convert(converter = UUIDBinaryConverter.class)
//    private UUID uniqeId;

    @Column(name = "token", length = 100, unique = true)
    private String token;

    private String fcmToken;

    private Boolean fcmEnable = true;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "service", nullable = false)
    private AccessTokenServiceType service = AccessTokenServiceType.LOGIN;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "app_user")
    private AppUser appUser;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "apiClient")
    private ApiClient apiClient;

    @JsonIgnore
    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "loginLog", referencedColumnName = "id")
    private LoginLog loginLog;

    private boolean revoked = false;

    private Date expiresAt;

    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "logouted_date")
    private LocalDateTime logoutedDate;

    @Column(name = "lastest_active")
    private LocalDateTime lastestActive;

    //    @GeneratedUuidV7
//    private UUID uniqeId;

    @Transient
    private boolean newToken;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AccessToken that = (AccessToken) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
