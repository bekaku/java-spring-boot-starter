package com.bekaku.api.spring.repository;

import com.bekaku.api.spring.model.AppUser;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserRepository extends BaseRepository<AppUser, Long>, JpaSpecificationExecutor<AppUser> {

    @Query("SELECT u FROM AppUser u WHERE u.email=?1 OR u.username=?1")
    Optional<AppUser> findByEmailOrUsername(String email);

    Optional<AppUser> findByUsername(String username);

    Optional<AppUser> findByEmail(String email);

    Optional<AppUser> findBySalt(String salt);

    @Modifying
    @Query("UPDATE AppUser e SET e.password = ?2 WHERE e = ?1")
    void updatePasswordBy(AppUser appUser, String password);

}
