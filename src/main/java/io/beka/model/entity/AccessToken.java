package io.beka.model.entity;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
@Entity
public class AccessToken {

    public AccessToken(Users users, String name , Date expiresAt, boolean revoked) {
        this.id = UUID.randomUUID().toString();
        this.users = users;
        this.name = name;
        this.expiresAt = expiresAt;
        this.revoked = revoked;
    }

    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "users")
    private Users users;

    private String name;

    @Column(columnDefinition = "tinyint(1) default 0")
    private Boolean revoked;

    @CreationTimestamp
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;

    private Date expiresAt;

}
