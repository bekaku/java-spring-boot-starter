package com.bekaku.api.spring.dto;


import com.bekaku.api.spring.enumtype.AiChatSourceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ChatSourceReference {

    private AiChatSourceType type;

    // document
    private String fileName;
    private String documentType;

    // database_table
    private String schema;
    private String tableName;

    private Double score;
}
