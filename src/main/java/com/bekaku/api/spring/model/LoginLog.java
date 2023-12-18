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
        indexes = {
                @Index(columnList = "deviceId"),
        }
)
public class LoginLog extends Id {

    public LoginLog(LoginLogType loginFrom, User user, IpAddress ipAddress, String deviceId, UserAgent userAgent) {
        this.loginFrom = loginFrom;
        this.user = user;
        if (ipAddress != null) {
            this.ip = ipAddress.getIp();
            this.hostName = ipAddress.getHostName();
        }
        this.deviceId = deviceId;
        this.userAgent = userAgent;
    }
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "userAgent")
    private UserAgent userAgent;

    @Column(columnDefinition = "tinyint(1) default 1")//1=Web, 2=ios , 3 = andriod
    private LoginLogType loginFrom;

    @Column(length = 50)
    private String ip;

    @Column(length = 100)
    private String hostName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user")
    private User user;

    @OneToOne(mappedBy = "loginLog")
    private AccessToken accessToken;

    @Column(length = 125)
    private String deviceId;

    @CreationTimestamp
    private Date createdAt;
}
