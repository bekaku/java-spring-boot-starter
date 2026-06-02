package com.bekaku.api.spring.properties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RagConfig {
    private int chunkSize;
    private int chunkSizeChar;
    private int chunkLengthEmbed;
}
