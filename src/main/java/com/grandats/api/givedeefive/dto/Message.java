package com.grandats.api.givedeefive.dto;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@JsonRootName("message")
@Data
public class Message {
    private String from;
    private String text;
    private String to;

    public Message() {

    }

    public Message(String from, String text, String to) {
        this.from = from;
        this.text = text;
        this.to = to;
    }
}
