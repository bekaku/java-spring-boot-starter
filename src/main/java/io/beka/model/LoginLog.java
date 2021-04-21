package io.beka.model;

import io.beka.vo.IpAddress;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class LoginLog extends BaseEntityId {

    public LoginLog(int loginForm, User user, IpAddress ipAddress) {
        this.loginForm = loginForm;
        this.user = user;
        this.ip = ipAddress.getIp();
        this.hostName = ipAddress.getHostName();
    }

    @Column(length = 1)
    private int loginForm;

    @Column(length = 50)
    private String ip;

    @Column(length = 100)
    private String hostName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user")
    private User user;

    @CreationTimestamp
    private Date createdAt;
}
