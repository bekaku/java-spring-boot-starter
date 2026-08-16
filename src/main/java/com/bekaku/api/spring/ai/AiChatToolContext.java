package com.bekaku.api.spring.ai;


import com.bekaku.api.spring.dto.ChatSourceReference;

import java.util.ArrayList;
import java.util.List;

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
