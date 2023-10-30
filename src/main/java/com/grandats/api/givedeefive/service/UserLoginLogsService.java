package com.grandats.api.givedeefive.service;

import com.grandats.api.givedeefive.model.UserLoginLogs;

import java.util.List;

public interface UserLoginLogsService extends BaseService<UserLoginLogs, UserLoginLogs> {
    List<UserLoginLogs> findByUserAndLoginDate(Long userId, String loginDateString);
}
