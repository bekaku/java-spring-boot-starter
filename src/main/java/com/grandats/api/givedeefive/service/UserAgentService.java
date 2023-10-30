package com.grandats.api.givedeefive.service;

import com.grandats.api.givedeefive.model.UserAgent;

import java.util.Optional;


public interface UserAgentService extends BaseService<UserAgent, UserAgent> {

    Optional<UserAgent> findByAgent(String name);

}
