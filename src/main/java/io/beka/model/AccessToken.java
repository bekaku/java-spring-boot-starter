package io.beka.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Date;
import java.util.UUID;

import static javax.persistence.FetchType.EAGER;
import static javax.persistence.FetchType.LAZY;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = {"token"}, callSuper = false)
@Entity
public class AccessToken extends BaseEntity {

    public AccessToken(User user, UserAgent userAgent, Date expiresAt, boolean revoked, ApiClient apiClient) {
        this.token = UUID.randomUUID().toString();
        this.user = user;
        this.userAgent = userAgent;
        this.expiresAt = expiresAt;
        this.revoked = revoked;
        this.apiClient = apiClient;
    }


    private String token;

    @Column(columnDefinition = "int(1) default 1", length = 1)
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

    @Column(columnDefinition = "tinyint(1) default 0")
    private Boolean revoked;

    private Date expiresAt;

}
