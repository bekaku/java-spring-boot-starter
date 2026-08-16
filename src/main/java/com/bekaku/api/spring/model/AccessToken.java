package com.bekaku.api.spring.model;

import com.bekaku.api.spring.annotation.GenSourceableTable;
import com.bekaku.api.spring.enumtype.AccessTokenServiceType;
import com.bekaku.api.spring.model.superclass.Id;
import com.bekaku.api.spring.util.DateUtil;
import com.bekaku.api.spring.util.HashUtil;
import com.bekaku.api.spring.util.UuidUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;

import static jakarta.persistence.FetchType.LAZY;


@Getter
@Setter
@NoArgsConstructor
@GenSourceableTable(createController = false, createDto = false, createPermission = false)
@Table(name = "access_token",
        comment = "Table for storing login token data.",
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
        String rawString = UuidUtils.generateUUID().toString();
        this.token = HashUtil.sha256(rawString);
        this.appUser = appUser;
        this.expiresAt = expiresAt;
        this.revoked = revoked;
        this.apiClient = apiClient;
        this.loginLog = loginLog;
        this.createdDate = createdDate;
        this.fcmToken = fcmToken;
        this.fcmEnable = true;
        this.lastestActive = DateUtil.getLocalDateTimeNow();
        this.rawToken = rawString;
    }

    public void onCreateToken(AppUser appUser, Date expiresAt, String token, AccessTokenServiceType service) {
        this.token = HashUtil.sha256(token);
        this.appUser = appUser;
        this.expiresAt = expiresAt;
        this.revoked = false;
        this.createdDate = DateUtil.getLocalDateTimeNow();
        this.fcmEnable = false;
        this.service = service;
        this.newToken = true;
        this.rawToken = token;
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

    @Column(name = "token", length = 100, unique = true, comment = "Hashed access token (SHA-256)")
    private String token;

    @Column(name = "fcm_token", comment = "Firebase Cloud Messaging token for device push notifications")
    private String fcmToken;

    @Column(name = "fcm_enable", comment = "Flag indicating if FCM notifications are enabled for this session")
    private Boolean fcmEnable = true;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "service", nullable = false, comment = "Service type of the token (e.g., LOGIN, REFRESH)")
    private AccessTokenServiceType service = AccessTokenServiceType.LOGIN;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "app_user", comment = "FK -> Ref table: app_user (id). The user who owns this token")
    private AppUser appUser;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "api_client", comment = "FK -> Ref table: api_client (id). The client application")
    private ApiClient apiClient;

    @JsonIgnore
    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "loginLog", referencedColumnName = "id", comment = "FK -> Ref table: login_log (id). The login activity log")
    private LoginLog loginLog;

    @Column(name = "revoked", comment = "Flag indicating whether this token has been revoked")
    private boolean revoked = false;

    @Column(name = "expires_at", comment = "Expiration timestamp of the token")
    private Date expiresAt;

    @Column(name = "created_date", updatable = false, comment = "Timestamp when the token was created")
    private LocalDateTime createdDate;

    @Column(name = "logouted_date", comment = "Timestamp when the session was logged out")
    private LocalDateTime logoutedDate;

    @Column(name = "lastest_active", comment = "Timestamp of the most recent activity with this token")
    private LocalDateTime lastestActive;

    //    @GeneratedUuidV7
//    private UUID uniqeId;

    @Transient
    private boolean newToken;

    @Transient
    private String rawToken;

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
