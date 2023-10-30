package com.bekaku.api.spring.dto;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;

@Data
@JsonRootName("user")
public class UserPersonalEditRequest {
    private String fullName;
    private String email;
    private String username;
    private String mobilePhone;
    private String positionName;
    private String teamLeaderName;
    private boolean initialConfig;
    private boolean autoFollowUser;
}
