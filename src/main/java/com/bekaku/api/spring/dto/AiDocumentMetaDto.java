package com.bekaku.api.spring.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Map;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@Accessors(chain = true)
public class AiDocumentMetaDto extends DtoId {

    private String fileMime;
    private String fileName;
    private boolean active;
    private Map<String, String> metadata;
}
