package com.bekaku.api.spring.service;

import com.bekaku.api.spring.model.UserLoginLogs;

import java.util.List;

public interface UserLoginLogsService extends BaseService<UserLoginLogs, UserLoginLogs> {
    List<UserLoginLogs> findByUserAndLoginDate(Long userId, String loginDateString);
}
