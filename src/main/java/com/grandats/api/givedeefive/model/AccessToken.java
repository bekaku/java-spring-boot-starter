package com.grandats.api.givedeefive.model;

import com.grandats.api.givedeefive.annotation.GenSourceableTable;
import com.grandats.api.givedeefive.model.superclass.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

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
        }
)
@Entity
public class AccessToken extends Id {

    public AccessToken(User user, UserAgent userAgent, Date expiresAt, boolean revoked, ApiClient apiClient, LoginLog loginLog, LocalDateTime createdDate, String fcmToken) {
        this.token = UUID.randomUUID().toString();
        this.user = user;
        this.userAgent = userAgent;
        this.expiresAt = expiresAt;
        this.revoked = revoked;
        this.apiClient = apiClient;
        this.loginLog = loginLog;
        this.createdDate = createdDate;
        this.fcmToken = fcmToken;
        this.fcmEnable = true;
    }

    @Column(name = "token", length = 100, unique = true)
    private String token;
    private String fcmToken;

    @Column(columnDefinition = "tinyint(1) default 1")
    private Boolean fcmEnable;

    @Column(columnDefinition = "int(1) default 1", length = 1, name = "service")
    private int service = 1;

    @ManyToOne
    @JoinColumn(name = "user")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "apiClient")
    private ApiClient apiClient;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "userAgent")
    private UserAgent userAgent;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "loginLog", referencedColumnName = "id")
    private LoginLog loginLog;

    @Column(columnDefinition = "tinyint(1) default 0", name = "revoked")
    private boolean revoked;

    private Date expiresAt;

    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "logouted_date")
    private LocalDateTime logoutedDate;

    @Column(name = "lastest_active")
    private LocalDateTime lastestActive;

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
