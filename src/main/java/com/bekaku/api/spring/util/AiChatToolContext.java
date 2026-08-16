package com.bekaku.api.spring.util;


import com.bekaku.api.spring.dto.ChatSourceReference;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.ArrayList;
import java.util.List;

@Component
@RequestScope
public class AiChatToolContext {
    private final List<ChatSourceReference> sources =
            new ArrayList<>();

    public void addSource(ChatSourceReference source) {
        sources.add(source);
    }

    public List<ChatSourceReference> getSources() {
        return List.copyOf(sources);
    }
}
