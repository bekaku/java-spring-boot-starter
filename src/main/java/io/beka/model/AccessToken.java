package io.beka.model;

import io.beka.annotation.GenSourceableTable;
import javax.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

import static javax.persistence.FetchType.LAZY;


@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = {"token"}, callSuper = false)
@GenSourceableTable(createController = false)
@Entity
public class AccessToken extends BaseId {

    public AccessToken(User user, UserAgent userAgent, Date expiresAt, boolean revoked, ApiClient apiClient, LoginLog loginLog) {
        this.token = UUID.randomUUID().toString();
        this.user = user;
        this.userAgent = userAgent;
        this.expiresAt = expiresAt;
        this.revoked = revoked;
        this.apiClient = apiClient;
        this.loginLog = loginLog;
    }


    @Column(name = "token", length = 100, unique = true)
    private String token;

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

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "loginLog", referencedColumnName = "id")
    private LoginLog loginLog;

    @Column(columnDefinition = "tinyint(1) default 0", name = "revoked")
    private Boolean revoked;

    private Date expiresAt;

}
