package com.bekaku.api.spring.model;

import com.bekaku.api.spring.annotation.GenSourceableTable;
import com.bekaku.api.spring.enumtype.LoginLogType;
import com.bekaku.api.spring.model.superclass.Id;
import com.bekaku.api.spring.vo.IpAddress;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

import static jakarta.persistence.FetchType.LAZY;

@GenSourceableTable(createController = false, createDto = false, createPermission = false)
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "login_log",
        comment = "Table for storing user login activity logs and device metadata.",
        indexes = {
                @Index(columnList = "deviceId"),
        }
)
public class LoginLog extends Id {

    public LoginLog(LoginLogType loginFrom, AppUser appUser, IpAddress ipAddress, String deviceId, UserAgent userAgent) {
        this.loginFrom = loginFrom;
        this.appUser = appUser;
        if (ipAddress != null) {
            this.ip = ipAddress.getIp();
            this.hostName = ipAddress.getHostName();
        }
        this.deviceId = deviceId;
        this.userAgent = userAgent;
    }
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "userAgent", comment = "FK -> Ref table: user_agent (id). Browser/client user agent information")
    private UserAgent userAgent;

    @Enumerated(EnumType.STRING)
    @Column(name = "login_from", comment = "Login source/platform type (e.g., WEB, MOBILE)")
    private LoginLogType loginFrom;

    @Column(name = "ip", length = 50, comment = "Client IP address used during login")
    private String ip;

    @Column(name = "host_name", length = 100, comment = "Resolved hostname from the client IP")
    private String hostName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_user", comment = "FK -> Ref table: app_user (id). The user who performed the login")
    private AppUser appUser;

    @OneToOne(mappedBy = "loginLog")
    private AccessToken accessToken;

    @Column(name = "device_id", length = 125, comment = "Unique hardware/app identifier of the login device")
    private String deviceId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, comment = "Timestamp when the login event occurred")
    private Date createdAt;
}
