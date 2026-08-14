package com.bekaku.api.spring.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * One SSE event. type distinguishes streamed answer tokens from the final
 * source list so the client can render citations once the answer is done.
 */
@Getter
@Builder
@AllArgsConstructor
public class ChatStreamEvent {

    private String type; // "token" | "sources" | "error" | "done"
    private String content;
}
