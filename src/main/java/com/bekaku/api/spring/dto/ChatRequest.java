package com.bekaku.api.spring.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ChatRequest {

    @NotBlank(message = "message is required")
    private String message;

    /**
     * Stable id used to group turns into one conversation for memory/context.
     * If omitted, a new one is generated per request (i.e. no memory across calls).
     */
    private Long conversationId;

    /**
     * Optional: restrict retrieval to a specific document by fileName.
     * Left null to search across the whole knowledge base.
     */
    private List<String> filterNames;


}
