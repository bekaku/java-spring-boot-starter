package io.beka.model.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
@Entity
public class AccessToken {

    public AccessToken(User user, String name , Date expiresAt, boolean revoked) {
        this.id = UUID.randomUUID().toString();
        this.user = user;
        this.name = name;
        this.expiresAt = expiresAt;
        this.revoked = revoked;
    }

    @Id
    private String id;


    @ManyToOne
    @JoinColumn(name = "user")
    private User user;

    @ManyToOne
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
