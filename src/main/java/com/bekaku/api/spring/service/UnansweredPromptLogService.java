package com.bekaku.api.spring.service;

import com.bekaku.api.spring.model.UnansweredPromptLog;

public interface UnansweredPromptLogService extends BaseService<UnansweredPromptLog, UnansweredPromptLog> {

    void logUnansweredPrompt(Long userId, String prompt);
}
