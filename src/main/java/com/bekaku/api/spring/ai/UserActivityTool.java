package com.bekaku.api.spring.ai;


import com.bekaku.api.spring.dto.OnlineUserDto;
import com.bekaku.api.spring.service.AccessTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class UserActivityTool {
    private final AccessTokenService accessTokenService;

    public UserActivityTool(AccessTokenService accessTokenService) {
        this.accessTokenService = accessTokenService;
    }
    @Tool(description = """
            Use this tool to get a list of currently online users.
            A user is considered 'online' if they have been active within the last 5 minutes.
            """)
    public List<OnlineUserDto> getOnlineUsers(ToolContext toolContext) {

        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);
        log.info("AI is checking for online users active since: {}", fiveMinutesAgo);

        List<OnlineUserDto> onlineUsers = accessTokenService.findOnlineUsers(fiveMinutesAgo);

        log.info("Found {} online users", onlineUsers.size());

        return onlineUsers;
    }
}
