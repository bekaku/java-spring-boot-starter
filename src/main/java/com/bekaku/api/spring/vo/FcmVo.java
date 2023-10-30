package com.bekaku.api.spring.vo;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@JsonRootName("fcmVo")
@NoArgsConstructor
public class FcmVo {

    public FcmVo(String subject, String content, Map<String, String> data, String image) {
        this.subject = subject;
        this.content = content;
        this.data = data;
        this.image = image;
    }

    private String subject;
    private String content;
    private Map<String, String> data;
    private String image;

    private int badge;
}
