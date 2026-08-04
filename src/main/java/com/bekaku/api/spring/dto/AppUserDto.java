package com.bekaku.api.spring.dto;

import com.bekaku.api.spring.enumtype.AppLocale;
import com.bekaku.api.spring.util.LongListToStringSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@JsonRootName("data")
@JsonIgnoreProperties(ignoreUnknown = true)
@Accessors(chain = true)
public class AppUserDto extends DtoId {
    private String email;
    private String username;
    private String token;
    private String fcmToken;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long accessTokenId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long avatarFileId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long coverFileId;
    private ImageDto avatar;
    private ImageDto cover;
    private Boolean active;

    private List<String> selectedRoles = new ArrayList<>();
    private AppLocale defaultLocale;
    private List<String> permissions = new ArrayList<>();
    private List<FavoriteMenuDto> favoriteMenus = new ArrayList<>();
    private boolean currentUser;
}
