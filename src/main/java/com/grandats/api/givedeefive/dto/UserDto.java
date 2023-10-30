package com.grandats.api.givedeefive.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.grandats.api.givedeefive.enumtype.AppLocale;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;



@Data
@JsonRootName("user")
@JsonIgnoreProperties(ignoreUnknown = true)
@Accessors(chain = true)
public class UserDto {
    private Long id;
    private String email;
    private String username;
    private String token;
    private String fcmToken;
    private Long accessTokenId;
    private Long avatarFileId;
    private Long coverFileId;
    private ImageDto avatar;
    private ImageDto cover;
    private Boolean active;
    private List<Long> selectedRoles = new ArrayList<>();
    private AppLocale defaultLocale;
}
