package io.beka.model;

import io.beka.annotation.GenSourceableTable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Sort;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@GenSourceableTable
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = {"salt"}, callSuper = false)
@Entity
public class User extends Auditable<Long> {
    public User(String username, String password, String email, Boolean status, String image) {
        this.salt = UUID.randomUUID().toString();
        this.username = username;
        this.password = password;
        this.email = email;
        this.image = image;
        this.status = status;
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

    @Basic(optional = false)
    @Column(nullable = false, length = 100, unique = true)
    private String username;

    private String password;

    @Column(length = 100, unique = true, nullable = false)
    private String email;

    private String image;

    private String salt;

    @Column(columnDefinition = "tinyint(1) default 1", nullable = false)
    private Boolean status;

//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
//    private List<AccessToken> accessTokens;

    @ManyToMany
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user"),
            inverseJoinColumns = @JoinColumn(name = "role"))
    private Set<Role> roles = new HashSet<>();

    public static Sort getSort() {
        return Sort.by(Sort.Direction.ASC, "username");
    }

}
