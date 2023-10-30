package com.grandats.api.givedeefive.model;

import com.grandats.api.givedeefive.annotation.GenSourceableTable;
import com.grandats.api.givedeefive.enumtype.LoginLogType;
import com.grandats.api.givedeefive.model.superclass.Id;
import com.grandats.api.givedeefive.vo.IpAddress;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@GenSourceableTable(createController = false, createDto = false, createPermission = false)
@NoArgsConstructor
@Getter
@Setter
@Entity
public class LoginLog extends Id {

    public LoginLog(LoginLogType loginFrom, User user, IpAddress ipAddress) {
        this.loginFrom = loginFrom;
        this.user = user;
        if (ipAddress != null) {
            this.ip = ipAddress.getIp();
            this.hostName = ipAddress.getHostName();
        }
    }

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

    @CreationTimestamp
    private Date createdAt;
}
