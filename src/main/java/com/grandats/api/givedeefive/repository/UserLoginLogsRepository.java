package com.grandats.api.givedeefive.repository;

import com.grandats.api.givedeefive.model.UserLoginLogs;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

@Repository
public interface UserLoginLogsRepository extends BaseRepository<UserLoginLogs, Long>, JpaSpecificationExecutor<UserLoginLogs> {

    @Query(value = "SELECT * FROM user_login_logs l WHERE l.user_id =?1 AND DATE(l.login_date) =?2", nativeQuery = true)
    List<UserLoginLogs> findByUserAndLoginDate(Long userId, String loginDateString);
}
