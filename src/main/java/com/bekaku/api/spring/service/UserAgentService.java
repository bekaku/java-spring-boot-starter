package com.bekaku.api.spring.service;

import com.bekaku.api.spring.model.UserAgent;

import java.util.Optional;


public interface UserAgentService extends BaseService<UserAgent, UserAgent> {

    Optional<UserAgent> findByAgent(String name);

}
