package com.bekaku.api.spring.dto;

import com.bekaku.api.spring.enumtype.AppLocale;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;


@Data
@JsonRootName("appUser")
@JsonIgnoreProperties(ignoreUnknown = true)
@Accessors(chain = true)
public class AppUserDto {
    private Long id;
    private String uuid;
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
    private List<String> permissions = new ArrayList<>();
    private List<FavoriteMenuDto> favoriteMenus = new ArrayList<>();
}
