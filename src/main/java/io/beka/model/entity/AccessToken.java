package io.beka.model.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

import static javax.persistence.FetchType.LAZY;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = {"token"})
@Entity
public class AccessToken {

    public AccessToken(User user, String name , Date expiresAt, boolean revoked) {
        this.token = UUID.randomUUID().toString();
        this.user = user;
        this.name = name;
        this.expiresAt = expiresAt;
        this.revoked = revoked;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;

    private String token;

    @ManyToOne
    @JoinColumn(name = "user")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "apiClient")
    private ApiClient apiClient;

    private String name;

    @Column(columnDefinition = "tinyint(1) default 0")
    private Boolean revoked;

    @CreationTimestamp
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;

    private Date expiresAt;

}
