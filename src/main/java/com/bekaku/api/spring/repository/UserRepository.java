package com.bekaku.api.spring.repository;

import com.bekaku.api.spring.model.User;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends BaseRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findBySalt(String salt);

    @Modifying
    @Query("UPDATE User e SET e.password = ?2 WHERE e = ?1")
    void updatePasswordBy(User user, String password);

}
