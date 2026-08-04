package com.bekaku.api.spring.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;

@Data
@JsonRootName("data")
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserPersonalEditRequest {
    private String fullName;
    private String email;
    private String username;
    private Long avatarFileId;
    private Long coverFileId;
}
