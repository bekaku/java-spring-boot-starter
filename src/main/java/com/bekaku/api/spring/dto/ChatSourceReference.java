package com.bekaku.api.spring.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ChatSourceReference {
    private String fileName;
    private String documentType;
    private Double score;
}
