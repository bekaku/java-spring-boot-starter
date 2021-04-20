package io.beka.service;

import io.beka.model.UserAgent;

import java.util.Optional;


public interface UserAgentService extends BaseService<UserAgent, UserAgent> {

    Optional<UserAgent> findByAgent(String name);

}
