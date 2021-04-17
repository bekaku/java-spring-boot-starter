package io.beka.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = {"salt"})
@Entity
public class User {
    public User(String username, String password, String email, Boolean status, String image, Set<Role> roles) {
        this.salt = UUID.randomUUID().toString();
        this.username = username;
        this.password = password;
        this.email = email;
        this.image = image;
        this.status = status;
        this.roles = roles;
    }

    public void update(String username, String password, String email, Boolean status, String image) {
        if (!"".equals(email)) {
            this.email = email;
        }
        this.status = status;
        if (!"".equals(username)) {
            this.username = username;
        }
        if (!"".equals(password)) {
            this.password = password;
        }
        if (!"".equals(image)) {
            this.image = image;
        }
    }

    //    @Id
    //    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;

    @Basic(optional = false)
    @Column(nullable = false)
    private String username;

    @Basic(optional = false)
    private String password;

    private String email;

    private String image;

    private String salt;

    @Column(columnDefinition = "tinyint(1) default 1")
    private Boolean status;

//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
//    private List<AccessToken> accessTokens;

    @ManyToMany
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user"),
            inverseJoinColumns = @JoinColumn(name = "role"))
    private Set<Role> roles;

    @CreationTimestamp
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;
}
