package com.bekaku.api.spring.repository;

import com.bekaku.api.spring.enumtype.AccessTokenServiceType;
import com.bekaku.api.spring.model.AccessToken;
import com.bekaku.api.spring.model.ApiClient;
import com.bekaku.api.spring.model.User;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccessTokenRepository extends BaseRepository<AccessToken, Long>, JpaSpecificationExecutor<AccessToken> {


    Optional<AccessToken> findByToken(String token);

    List<AccessToken> findAllByFcmToken(String fcmToken);

    @Query("SELECT a FROM AccessToken a WHERE a.token =?1 AND a.revoked =?2 ")
    Optional<AccessToken> findAccessTokenByToken(String token, boolean revoked);

    @Query("SELECT a FROM AccessToken a WHERE a.user =?1 and  a.token =?2 AND a.revoked = false ")
    Optional<AccessToken> findAccessTokenByTokenAndUser(User user, String token);

    @Query("SELECT a FROM AccessToken a WHERE a.user =?1 AND a.service=?2 AND a.revoked = false ORDER BY a.id DESC LIMIT 1 ")
    Optional<AccessToken> findLatestAccessTokenByUser(User user, AccessTokenServiceType service);

    Optional<AccessToken> findByTokenAndRevoked(String token, boolean revoked);

    @Modifying
    @Query("UPDATE AccessToken a SET a.lastestActive = ?1 WHERE a.id = ?2")
    void updateLastestActive(LocalDateTime lastestActive, Long id);

    @Query("SELECT a FROM AccessToken a WHERE a.user.id =?1 AND a.service=?2 AND a.revoked=?3 ORDER BY a.lastestActive DESC, a.id DESC ")
    List<AccessToken> findAllByUserAndRevoked(Long userId, AccessTokenServiceType service, boolean revoked);

    @Query(value = "SELECT a.fcm_token FROM access_token a " +
            "WHERE a.`user` = ?1 AND a.revoked IS FALSE AND a.fcm_enable IS TRUE AND a.fcm_token IS NOT NULL", nativeQuery = true)
    List<String> findAllFcmTokenByUserId(Long userId);

    void deleteByToken(String token);

    void deleteByApiClient(ApiClient apiClient);

    @Modifying
//    @Query("UPDATE AccessToken a SET a.revoked = true where a.user.id = ?1 AND a.revoked = false")
    @Query(value = "UPDATE access_token SET revoked=true WHERE user=?1 AND revoked is false ", nativeQuery = true)
    void revokeTokenByUserId(Long userId);

    @Modifying
    @Query(value = "UPDATE access_token SET fcm_token=null WHERE fcm_token=?1", nativeQuery = true)
    void updateNullFcmToken(String fcmToken);
}
